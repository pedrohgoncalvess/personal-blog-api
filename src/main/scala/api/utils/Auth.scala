package api.utils

import akka.http.scaladsl.server.directives.Credentials
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.{JWTClaimsSet, SignedJWT}
import com.typesafe.config.{Config, ConfigFactory}
import database.models.Token
import database.operations.TokenQ
import java.math.BigInteger
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.time.format.DateTimeFormatter
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class Authentication {

  private val config: Config = ConfigFactory.load()

  val modulus = config.getString("MODULUS_KEY")
  val publicExponent = config.getString("PUBLIC_EXPONENT")

  val publicKeySpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent))
  val publicKeyFactory = KeyFactory.getInstance("RSA")
  val publicKey: RSAPublicKey = publicKeyFactory.generatePublic(publicKeySpec).asInstanceOf[RSAPublicKey]

  def validateToken(token: String): Boolean = {
    val signedJWT = SignedJWT.parse(token)

    val verifier: JWSVerifier = new RSASSAVerifier(publicKey)

    if (signedJWT.verify(verifier)) {
      val claims: JWTClaimsSet = signedJWT.getJWTClaimsSet
      val expirationTime: Long = claims.getExpirationTime.getTime
      val currentTime: Long = System.currentTimeMillis()
      if (currentTime < expirationTime) true else false
    } else false

  }

  def verifyToken(token:String): Boolean = {

    val tkn = new TokenQ
    val getToken:Future[Token] = tkn.getTokenByToken(token)
    try {
      val result: Token = Await.result(getToken, 5.seconds)
      result.token == token && java.time.LocalDateTime.parse(result.expire,DateTimeFormatter.ISO_OFFSET_DATE_TIME).isBefore(java.time.LocalDateTime.now)

    } catch {
      case _: Throwable => false
    }
  }

  def myUserPassAuthenticator(credentials: Credentials): Option[Boolean] = {

    credentials match {
      case p@Credentials.Provided(_) if p.provideVerify(verifier=validateToken) => Some(true)
      case _ => None
    }
  }
}
