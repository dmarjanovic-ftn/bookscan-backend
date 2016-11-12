package authentication

import javax.inject.Inject

import authentikat.jwt.JsonWebToken
import dao.UserDAO
import models.User
import play.api.mvc._

import scala.concurrent.Future

case class TokenRequest[A](email: String, request: Request[A]) extends WrappedRequest[A](request)

class Authentication @Inject()(val userDAO: UserDAO) {
  case class HasToken[A] (action: Action[A]) extends Action[A] {
    def apply(request: Request[A]): Future[Result] = {
      request.headers.get("X-AUTH-TOKEN") map { token =>
        val claims: Option[Map[String, String]] = token match {
          case JsonWebToken(header, claimsSet, signature) =>
            claimsSet.asSimpleMap.toOption
          case x =>
            None
        }

        val email = claims.getOrElse(Map.empty[String, String]).get("email")
        action(TokenRequest(email.get, request))
      } getOrElse {
        Future.successful(Results.Unauthorized("401 No Security Token\n"))
      }
    }

    lazy val parser = action.parser
  }
}