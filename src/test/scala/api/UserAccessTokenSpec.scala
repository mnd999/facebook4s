package api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import config.FacebookConfig._
import domain.UserAccessToken
import org.f100ded.scalaurlbuilder.URLBuilder
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{AsyncWordSpec, Matchers}
import services.{AsyncRequestService, FacebookInternals}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.io.Source

class UserAccessTokenSpec
  extends AsyncWordSpec with Matchers with MockitoSugar {

  trait ClientProbe extends FacebookInternals {
    val facebookServices = mock[FacebookInternals]
    val mockAsyncRequestService = mock[AsyncRequestService]



    def mockSendWithResource(resourcePath: String) = {
      when(mockAsyncRequestService.sendRequest(anyObject[URLBuilder])).thenReturn(
        Future.successful(
          HttpResponse(
            entity = HttpEntity(
              contentType = ContentTypes.`application/json`,
              string      = Source.fromResource(resourcePath).mkString)
          )
        )
      )
    }

    def mockSendError(resourcePath: String) = {
      when(mockAsyncRequestService.sendRequest(anyObject[URLBuilder])).thenReturn(
        Future.successful(
          HttpResponse(
            status = StatusCodes.BadRequest,
            entity = HttpEntity(
              contentType = ContentTypes.`application/json`,
              string      = Source.fromResource(resourcePath).mkString)
          )
        )
      )
    }

    override implicit lazy val system = ActorSystem()
    override implicit lazy val mat = ActorMaterializer()
    override implicit lazy val ec = system.dispatcher
    override val asyncRequestService = mockAsyncRequestService
    override val transformer = new DomainTransformer()
  }


  "Facebook Graph Api" should {
    "return user access token" in {
      val client = new FacebookClient(clientId, appSecret) with ClientProbe

      client.mockSendWithResource(resourcePath = "testdata/user_access_token.json")
      client.userAccessTokenEither("code") map {
        case Right(token) =>
          token.tokenValue.value shouldBe "test token"
          token.tokenType shouldBe UserAccessToken("bearer", 5107587.seconds)
         case Left(e) => fail(e.error.message)
      }
    }

    "return error in wrong code" in {
      val client = new FacebookClient(clientId, appSecret) with ClientProbe

      client.mockSendError(resourcePath = "testdata/user_access_token_wrong_code.json")
      client.userAccessTokenEither("code") map {
        case Right(token) => fail("left expected")
        case Left(e) => e.error.message shouldBe "Invalid verification code format."
      }
    }
  }

}
