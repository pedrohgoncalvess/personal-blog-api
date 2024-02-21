package api.routes.auth

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import api.json.{Auth, AuthJsonSupport}
import com.password4j.Password
import database.operations.AuthQ
import java.util.{Date, UUID}
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.{JWSAlgorithm, JWSHeader}
import com.nimbusds.jwt.{JWTClaimsSet, SignedJWT}
import com.typesafe.config.{Config, ConfigFactory}
import java.math.BigInteger
import java.security.{KeyFactory, PrivateKey}
import java.security.spec.RSAPrivateKeySpec
import java.time.{Instant, ZoneId}


class Post extends Directives with AuthJsonSupport {

  private val opr = new AuthQ

  private val config: Config = ConfigFactory.load()

  private val modulus = config.getString("MODULUS_KEY")
  private val privateExponent = config.getString("PRIVATE_EXPONENT")

  def generateAccessToken(userId: String, admin: Boolean): (String, String) = {
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

  val route: Route =
    path("auth") {
      post {
        entity(as[Auth]) { auth =>
          val user = opr.getUserByUsername(auth.username)
          if (Password.check(auth.password, user.password).withArgon2()) {
            val infoToken = generateAccessToken(user.id, user.admin)
            complete(StatusCodes.OK,Map("token" -> infoToken._1, "refresh_token" -> "", "expiration_time" -> infoToken._2))
          } else {
            complete(StatusCodes.Unauthorized)
          }
        }
      }
    }
}
