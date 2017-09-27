package api

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import play.api.libs.json.Reads
import cats.implicits._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import services.AsyncRequestService

import scala.concurrent.Future

class JsonUnmarshaler(asyncRequestService: AsyncRequestService) extends PlayJsonSupport {

  import asyncRequestService._

  def parseResponse[E, T](response: HttpResponse)(errorFormatter: String => Future[Either[E, T]])
    (implicit reads: Reads[T], reads1: Reads[E]): Future[Either[E, T]] = {
    def parseFE(httpEntity: HttpEntity): Future[Either[E, T]] = Unmarshal[HttpEntity](
      httpEntity.withContentType(ContentTypes.`application/json`)).to[T] map(_.asRight) recoverWith {
      case e => errorFormatter(e.getLocalizedMessage)
    }

    def parseFL(httpEntity: HttpEntity): Future[Either[E, T]] = Unmarshal[HttpEntity](
      httpEntity.withContentType(ContentTypes.`application/json`)).to[E] map(_.asLeft) recoverWith {
      case e => errorFormatter(e.getLocalizedMessage)
    }

    response.status  match {
      case StatusCodes.OK                  => parseFE(response.entity)
      case StatusCodes.BadRequest          => parseFL(response.entity)
      case StatusCodes.InternalServerError => errorFormatter("Internal server error.")
      case _                               => errorFormatter("Unknown exception")
    }
  }

}