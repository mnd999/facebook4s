package api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import config.FacebookConfig.{appSecret, clientId}
import org.f100ded.scalaurlbuilder.URLBuilder
import org.mockito.Matchers.anyObject
import org.mockito.Mockito.when
import org.scalatest._
import org.scalatest.mockito.MockitoSugar
import services.{AsyncRequestService, FacebookInternals}

import scala.concurrent.Future
import scala.io.Source


trait FacebookClientSpec extends fixture.AsyncWordSpec with Matchers {

  type FixtureParam = ClientProbe with FacebookClient

  def withFixture(test: OneArgAsyncTest): FutureOutcome = {
    withFixture(test.toNoArgAsyncTest(ClientProbe()))
  }

}

trait ClientProbe extends FacebookInternals with MockitoSugar {
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

object ClientProbe {
  def apply() = new FacebookClient(clientId, appSecret) with ClientProbe
}