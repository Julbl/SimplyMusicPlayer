import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplaylist.MediaPlayerManager
import com.example.simplymusicplayer.MusicTrack
import com.example.simplymusicplayer.R

class TrackAdapter(
    private val context: Context,
    private val allTracks: List<MusicTrack>,
    private val onTrackClickListener: (MusicTrack) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    private var filteredTracks: List<MusicTrack> = allTracks
    private val mediaPlayerManager = MediaPlayerManager.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = filteredTracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            mediaPlayerManager.playOrPauseTrack(context, track)
            mediaPlayerManager.updateTrack(track)
            onTrackClickListener(track)
        }
    }

    override fun getItemCount(): Int {
        return filteredTracks.size
    }


    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.trackTitleTextView)
        private val artistTextView: TextView = itemView.findViewById(R.id.trackArtistTextView)
        private val albumTextView: TextView = itemView.findViewById(R.id.trackAlbumTextView)
        private val albumImageView: ImageView = itemView.findViewById(R.id.albumImageView)

        fun bind(track: MusicTrack) {
            titleTextView.text = track.title
            artistTextView.text = track.artist
            albumTextView.text = track.album
            albumImageView.setImageResource(track.imageResourse)
        }
    }

    fun updateData(tracks: List<MusicTrack>) {
        filteredTracks = tracks.toMutableList()
        notifyDataSetChanged()
    }

}
