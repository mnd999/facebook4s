package client

import java.net.URL

import base.{FacebookClientSupport, toInstant}
import base.TestConfiguration._
import cats.implicits._
import client.feed._
import domain.FacebookPaging
import domain.friends.{FacebookFriends, FacebookFriendsSummary}
import domain.profile._
import org.apache.commons.lang3.LocaleUtils

class FriendsSpec extends FacebookClientSupport {
  val friendId = FacebookUserId("595040754173861")

  val friend = FacebookUser(
    friendId, None, "Valeryi Baibossynov".some,
    Some(FacebookUserPicture(50.0, isSilhouette = false, new URL("https://lookaside.facebook.com/platform/profilepic/"), 50.0)),
    Some("Valeryi"), Some("Baibossynov"), link = Some(new URL("https://www.facebook.com")), None,
    Some(LocaleUtils.toLocale("en_US")), None, Gender.Male.some, AgeRange(21, None).some,
    Some(Cover(None,0.0,0.0, new URL("https://lookaside.facebook.com/platform/coverpic/"))),
    Some(toInstant("2017-11-11T00:10:08+0000")))

  val friendPaging = FacebookPaging(
    "QVFIUjlua0ltaGRGXzdlT295V25GX01ZAV1dJczhsNmxBSlBWcWRDS0RET2x1VXNmOXVGczhEZAzJDbmdSNFBRVURNZA0pES2hKRWZACblRBUlZA0ZAnl0YUJvVUd3".some,
    "QVFIUjlua0ltaGRGXzdlT295V25GX01ZAV1dJczhsNmxBSlBWcWRDS0RET2x1VXNmOXVGczhEZAzJDbmdSNFBRVURNZA0pES2hKRWZACblRBUlZA0ZAnl0YUJvVUd3".some)

  val friendsSummary = FacebookFriendsSummary(totalCount = 1)

  val facebookFriends = FacebookFriends(
    friends = List(friend),
    paging = friendPaging.some,
    summary = friendsSummary.some
  )

  "Graph Api" should {
    "return user friends" in { c =>
      c.friends(FacebookUserId("me"), userAccessToken).map(_.withoutQueryParams shouldBe facebookFriends)
    }

    "return user friends result" in { c =>
      c.friendsResult(FacebookUserId("me"), userAccessToken).map(_.map(_.withoutQueryParams) shouldBe facebookFriends.asRight)
    }
  }
}
