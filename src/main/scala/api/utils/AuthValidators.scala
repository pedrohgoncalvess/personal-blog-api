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
import java.time.{Instant, LocalDateTime, ZoneId}
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.{JWSAlgorithm, JWSHeader}

import java.util.{Date, UUID}


case class AuthInfo(userId: UUID, isAdmin: Boolean)


object AuthValidators {

  private val config: Config = ConfigFactory.load()
  private def hexToBigInteger(hexString: String): BigInteger = {
    val cleanHex = hexString.replaceAll("[:\\s]", "")
    new BigInteger(cleanHex, 16)
  }

  private val modulusHex = config.getString("MODULUS_KEY")
  private val publicExponent = config.getString("PUBLIC_EXPONENT")

  private val modulusBigInt = hexToBigInteger(modulusHex)
  private val publicExponentBigInt = new BigInteger(publicExponent)

  private val publicKeySpec = new RSAPublicKeySpec(modulusBigInt, publicExponentBigInt)
  private val publicKeyFactory = KeyFactory.getInstance("RSA")
  private val publicKey: RSAPublicKey = publicKeyFactory.generatePublic(publicKeySpec).asInstanceOf[RSAPublicKey]

  private def validateToken(token: String): Boolean = {
    val signedJWT = SignedJWT.parse(token)

    val verifier: JWSVerifier = new RSASSAVerifier(publicKey)

    if (signedJWT.verify(verifier)) {
      val claims: JWTClaimsSet = signedJWT.getJWTClaimsSet
      val expirationTime: Long = claims.getExpirationTime.getTime
      val currentTime: Long = System.currentTimeMillis()
      if (currentTime < expirationTime) true else false
    } else false

  }

  def authenticator(credentials: Credentials): Option[AuthInfo] = {
    credentials match {
      case p@Credentials.Provided(token) if validateToken(token) => {
        try {
          val signedJWT = SignedJWT.parse(token)
          val claims = signedJWT.getJWTClaimsSet
          val userId = UUID.fromString(claims.getStringClaim("id"))
          val isAdmin = claims.getBooleanClaim("admin")
          Some(AuthInfo(userId, isAdmin))
        } catch {
          case _: Exception => None
        }
      }
      case _ => None
    }
  }

  def generateAccessToken(userId: UUID, admin: Boolean): (String, LocalDateTime) =
      val config: Config = ConfigFactory.load()
    
      val modulus = new BigInteger(config.getString("MODULUS_KEY"), 16)
      val privateExponent = new BigInteger(config.getString("PRIVATE_EXPONENT"), 16)

      val jwsAlgorithm: JWSAlgorithm = JWSAlgorithm.RS256
    
      val privateKeySpec = new RSAPrivateKeySpec(modulus, privateExponent)
      val keyFactory = KeyFactory.getInstance("RSA")
      val privateKey = keyFactory.generatePrivate(privateKeySpec)
    
      val jwsHeader = new JWSHeader.Builder(jwsAlgorithm)
        .keyID(UUID.randomUUID().toString)
        .build()
    
      val expirationTime = Date.from(Instant.now().plusSeconds(7200))
    
      val payload = new JWTClaimsSet.Builder()
        .claim("id", userId.toString)
        .claim("admin", admin)
        .expirationTime(expirationTime)
        .build()
    
      val signedJWT = new SignedJWT(jwsHeader, payload)
      signedJWT.sign(new RSASSASigner(privateKey))
    
      (
        signedJWT.serialize(),
        expirationTime.toInstant.atZone(ZoneId.systemDefault).toLocalDateTime
      )
  
}
