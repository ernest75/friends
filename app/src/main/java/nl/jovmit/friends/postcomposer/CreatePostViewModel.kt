package nl.jovmit.friends.postcomposer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nl.jovmit.friends.domain.exceptions.BackendException
import nl.jovmit.friends.domain.exceptions.ConnectionUnavailableException
import nl.jovmit.friends.domain.post.Post
import nl.jovmit.friends.domain.user.InMemoryUserData
import nl.jovmit.friends.infrastructure.Clock
import nl.jovmit.friends.infrastructure.IdGenerator
import nl.jovmit.friends.postcomposer.state.CreatePostState

class CreatePostViewModel(
  private val userData: InMemoryUserData,
  private val clock: Clock,
  private val idGenerator: IdGenerator
) {

  private val mutablePostState = MutableLiveData<CreatePostState>()
  val postState: LiveData<CreatePostState> = mutablePostState

  fun createPost(postText: String) {
    try {
      val post = addPost(userData.loggedInUserId(), postText)
      mutablePostState.value = CreatePostState.Created(post)
    } catch (backendException: BackendException) {
      mutablePostState.value = CreatePostState.BackendError
    } catch (offlineException: ConnectionUnavailableException) {
      mutablePostState.value = CreatePostState.Offline
    }
  }

  private fun addPost(userId: String, postText: String): Post {
    if (postText == ":backend:") {
      throw BackendException()
    } else if (postText == ":offline:") {
      throw ConnectionUnavailableException()
    }
    val timestamp = clock.now()
    val postId = idGenerator.next()
    return Post(postId, userId, postText, timestamp)
  }
}
