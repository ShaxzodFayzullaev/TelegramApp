package shakha.telegram.db

import shakha.telegram.models.Profile

interface MyDbInterface {
fun addPro(profile: Profile)
fun getAllPro():ArrayList<Profile>
fun editPro(profile: Profile)
fun deletePro(profile: Profile)

}