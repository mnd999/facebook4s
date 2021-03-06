package domain.comments

import java.time.Instant

import domain.media.FacebookAttachment
import domain.profile.FacebookProfileId
import domain.{FacebookOrder, FacebookPaging}

final case class FacebookCommentId(value: String)

final case class FacebookComment(
  id          : FacebookCommentId,
  message     : Option[String],
  createdTime : Option[Instant],
  from        : Option[FacebookProfileId],
  parent      : Option[FacebookComment],
  mediaObject : Option[FacebookMediaObject],
  attachment  : Option[FacebookAttachment])

final case class FacebookComments(
  comments : List[FacebookComment],
  paging   : Option[FacebookPaging],
  summary  : Option[FacebookCommentSummary] = None)

final case class FacebookCommentSummary(
  order: FacebookOrder,
  totalCount: Int,
  canComment: Option[Boolean])

final case class FacebookMediaObjectId(value: String)

final case class FacebookMediaObject(id: FacebookMediaObjectId, createdTime: Instant)