package api.utils

import akka.http.scaladsl.server.directives.Credentials
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.{JWTClaimsSet, SignedJWT}
import com.typesafe.config.{Config, ConfigFactory}

import java.math.BigInteger
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.security.PrivateKey
import java.security.spec.RSAPrivateKeySpec
import java.time.{Instant, ZoneId}
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.{JWSAlgorithm, JWSHeader}
import java.util.{Date, UUID}

class AuthValidators {

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

  def myUserPassAuthenticator(credentials: Credentials): Option[Boolean] = {

    credentials match {
      case p@Credentials.Provided(_) if p.provideVerify(verifier=validateToken) => Some(true)
      case _ => None
    }
  }

  def generateAccessToken(userId: String, admin: Boolean): (String, String) = {

    val config: Config = ConfigFactory.load()

    val modulus = config.getString("MODULUS_KEY")
    val privateExponent = config.getString("PRIVATE_EXPONENT")

    val jwsAlgorithm: JWSAlgorithm = JWSAlgorithm.RS256

    val privateKeySpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privateExponent))
    val privateKeyFactory = KeyFactory.getInstance("RSA")
    val privateKey: PrivateKey = privateKeyFactory.generatePrivate(privateKeySpec)

    val jwsHeader = new JWSHeader.Builder(jwsAlgorithm).keyID(UUID.randomUUID().toString).build()
    val expirationTime = Date.from(Instant.now().plusSeconds(7200))

    val payload: JWTClaimsSet = new JWTClaimsSet.Builder()
      .claim("id", userId)
      .claim("admin", admin)
      .expirationTime(expirationTime)
      .build()

    val signedJWT = new SignedJWT(jwsHeader, payload)
    signedJWT.sign(new RSASSASigner(privateKey))

    (signedJWT.serialize(),expirationTime.toInstant.atZone(ZoneId.systemDefault).toLocalDateTime.toString)
  }
}
