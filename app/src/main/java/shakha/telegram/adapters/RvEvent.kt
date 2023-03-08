package shakha.telegram.adapters

import android.widget.ImageView
import shakha.telegram.models.Profile

interface RvEvent {
    fun menuClick(info: Profile, more: ImageView)
}
