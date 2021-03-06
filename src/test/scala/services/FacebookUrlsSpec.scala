package services

import base._
import cats.implicits._
import config.FacebookConfig._
import domain.comments.FacebookCommentsAttributes
import domain.oauth.FacebookToken
import domain.permission.FacebookPermissions.UserDataPermissions.Posts
import domain.posts.{FacebookPostAttributes, FacebookPostId}
import domain.profile.{FacebookProfileId, FacebookUserId}
import org.scalatest.{Matchers, WordSpec}
import serialization.compatibility._
import syntax.FacebookShowOps._

class FacebookUrlsSpec extends WordSpec with Matchers {

  val v = version.value

  "FacebookUrls" should {
    "return url to obtain log lived uri" in {
      TestUrls.longLivedTokenUri("test").toString() shouldBe s"https://graph.facebook.com/v$v/oauth/access_token" +
        s"?client_id=${clientId.show}" +
        s"&client_secret=${appSecret.show}" +
        s"&grant_type=fb_exchange_token" +
        s"&fb_exchange_token=test"
    }

    "return post uri" in {
      TestUrls.postUri(FacebookPostId("postId"), userAccessToken, Nil).toString() shouldBe "https://graph.facebook.com" +
        s"/v$v/postId?access_token=token"
    }


    "return feed uri" in {
      TestUrls.userFeedUri(
        userAccessToken,
        FacebookUserId("me"),
        FacebookPostAttributes.defaultPostAttributeValues).toString() shouldBe "https://graph.facebook.com" +
        s"/v$v/me/feed?access_token=token&fields=id%2Cstory%2Ccreated_time%2Cobject_id%2Cpicture%2Cfrom"
    }

    "return auth uri" in {
      TestUrls.buildAuthUrl(Seq.empty).toString() shouldBe s"https://facebook.com/v$v/dialog/oauth" +
        s"?client_id=${clientId.show}" +
        s"&redirect_uri=http%3A%2F%2Flocalhost%3A9000%2Fredirect" +
        s"&response_type=code"
    }

    "return auth uri with state, response_type and permissions" in {
      TestUrls.buildAuthUrl(
        permissions  = Seq(Posts),
        responseType = FacebookToken,
        state        = "asd".some).toString() shouldBe s"https://facebook.com/v$v/dialog/oauth" +
        s"?client_id=${clientId.show}" +
        s"&redirect_uri=http%3A%2F%2Flocalhost%3A9000%2Fredirect" +
        s"&response_type=${FacebookToken.value}" +
        s"&scope=${Posts.value}" +
        s"&state=asd"
    }

    "return likes uri" in {
      TestUrls.likesUri(FacebookPostId("postId"), userAccessToken).toString() shouldBe "https://graph.facebook.com" +
        s"/v$v/postId/likes?access_token=token&summary=false"
    }

    "return likes uri with summary" in {
      TestUrls.likesUri(FacebookPostId("postId"), userAccessToken, summary = true).toString() shouldBe "https://graph." +
        s"facebook.com/v$v/postId/likes?access_token=token&summary=true"
    }

    "return comment uri" in {
      TestUrls.commentsUri(FacebookPostId("postId"), userAccessToken,
        FacebookCommentsAttributes.defaultCommentsAttributeValues).toString() shouldBe "https://graph.facebook.com" +
        s"/v$v/postId/comments?access_token=token&summary=false" +
        "&fields=id%2Cmessage%2Ccreated_time%2Cattachment%2Cfrom%2Cobject"
    }

    "return comment uri with summary" in {
      TestUrls.commentsUri(FacebookPostId("postId"), userAccessToken,
        FacebookCommentsAttributes.defaultCommentsAttributeValues, summary = true).toString() shouldBe "https://graph." +
        s"facebook.com/v$v/postId/comments?access_token=token&summary=true" +
        "&fields=id%2Cmessage%2Ccreated_time%2Cattachment%2Cfrom%2Cobject"
    }

    "return albums uri" in {
      TestUrls.albumsUri(FacebookProfileId("profileId"), userAccessToken).toString() shouldBe "https://graph." +
        s"facebook.com/v$v/profileId/albums?access_token=token"
    }

    "return taggable_friends uri" in {
      TestUrls.friendsUri(userAccessToken, FacebookUserId("123"), Nil).toString() shouldBe "https://graph." +
        s"facebook.com/v$v/123/friends?access_token=token"
    }

  }

}
