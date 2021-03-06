package serialization.compatibility

import java.net.URL

import domain.media._
import io.circe._
import io.circe.generic.semiauto._
import serialization.FacebookDecoders._

class AttachmentCompatSpec extends CompatibilitySpec {

  case class TestObject(attachmentType: AttachmentType)

  implicit val decodeTestObject: Decoder[TestObject] = deriveDecoder[TestObject]

  val attachmentTargetPath = "testdata/attachment_target.json"
  val attachmentImagePath = "testdata/attachment_image.json"
  val attachmentPath = "testdata/comment_attachment.json"

  val imageSource = FacebookImageSource(720,
    new URL("https://scontent.xx.fbcdn.net/v/t1.0-9/26169805_135224317270265_2857586441485590537_n.jpg" +
      "?oh=97edfd66290b3e4112a8731e8cd2b5fb&oe=5B0A23AC"), 104)

  val attachmentTarget = FacebookAttachmentTarget(FacebookAttachmentId("135224317270265"),
    new URL("https://www.facebook.com/photo.php?fbid=135224317270265&set=p.135224317270265&type=3"))


  val facebookAttachment = FacebookAttachment(imageSource, attachmentTarget, attachmentTarget.url, AttachmentTypes.Photo)

  "FacebookAttachmentTarget" should {
    s"be compatible with $attachmentTargetPath" in {
      decodeJson[FacebookAttachmentTarget](attachmentTargetPath) shouldBe attachmentTarget
    }

    s"be compatible with $attachmentImagePath" in {
      decodeJson[FacebookImageSource](attachmentImagePath) shouldBe imageSource
    }

    s"be compatible with $attachmentPath" in {
      decodeJson[FacebookAttachment](attachmentPath) shouldBe facebookAttachment
    }

    s"attachment types should be compatible" in {
      decodeType("video_inline") shouldBe TestObject(AttachmentTypes.Video)
      decodeType("photo") shouldBe TestObject(AttachmentTypes.Photo)
      decodeType("sticker") shouldBe TestObject(AttachmentTypes.Sticker)
      decodeType("animated_image_autoplay") shouldBe TestObject(AttachmentTypes.GIF)
    }
  }

  private[this] def decodeType(attachmentTypeRaw: String) = {
    decodeStringJson[TestObject]("{ \"attachmentType\": \"" + attachmentTypeRaw + "\"}")
  }
}
