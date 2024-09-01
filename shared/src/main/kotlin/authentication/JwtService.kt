package example.com.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import example.com.model.User
import java.util.Date


class JwtService {

    private val issuer = "auto-triager-server"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC256(jwtSecret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withAudience("all")
        .build()

    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject(user._id.toString())
            .withIssuer(issuer)
            .withClaim("email", user.email)
            .withClaim("role", user.role.name)
            .withClaim("orgId", user.organisationId)
            .withClaim("teamIds", listOf(user.teamIds))
            .withExpiresAt(Date(System.currentTimeMillis() + (1000*60*60))) // Token valid for 10 minutes
            .sign(algorithm)
    }
}