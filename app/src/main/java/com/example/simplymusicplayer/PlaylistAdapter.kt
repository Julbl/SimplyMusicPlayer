import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplaylist.Playlist
import com.example.simplymusicplayer.R

class PlaylistAdapter(private val playlists: List<Playlist>, private val onItemClick: (Playlist) -> Unit) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener { onItemClick(playlist) }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playlistImageView: ImageView = itemView.findViewById(R.id.playlistImageView)
        private val playlistTitleTextView: TextView = itemView.findViewById(R.id.playlistTitleTextView)

        fun bind(playlist: Playlist) {
            playlistImageView.setImageResource(playlist.coverImageUrl)
            playlistTitleTextView.text = playlist.name
        }
    }
}