package nl.jovmit.friends.domain.people

import nl.jovmit.friends.domain.exceptions.BackendException
import nl.jovmit.friends.domain.exceptions.ConnectionUnavailableException
import nl.jovmit.friends.people.state.PeopleState

class PeopleRepository(
  private val peopleCatalog: InMemoryPeopleCatalog
) {

  fun loadPeopleFor(userId: String): PeopleState {
    return try {
      val peopleForUserId = peopleCatalog.loadPeopleFor(userId)
      PeopleState.Loaded(peopleForUserId)
    } catch (backendException: BackendException) {
      PeopleState.BackendError
    } catch (offlineException: ConnectionUnavailableException) {
      PeopleState.Offline
    }
  }
}