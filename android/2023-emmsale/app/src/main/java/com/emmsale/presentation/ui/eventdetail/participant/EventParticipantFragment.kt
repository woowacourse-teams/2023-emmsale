import android.os.Bundle
import com.emmsale.R
import com.emmsale.databinding.FragmentEventParticipantBinding
import com.emmsale.presentation.base.fragment.BaseFragment

class EventParticipantFragment : BaseFragment<FragmentEventParticipantBinding>() {
    override val layoutResId: Int = R.layout.fragment_event_participant

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"

        fun create(eventId: Long): EventParticipantFragment {
            val fragment = EventParticipantFragment()
            fragment.arguments = Bundle().apply {
                putLong(EVENT_ID_KEY, eventId)
            }
            return fragment
        }
    }
}
