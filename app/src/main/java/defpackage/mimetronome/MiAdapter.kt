package defpackage.mimetronome

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.View
import android.view.ViewGroup
import defpackage.mimetronome.base.BaseAdapter
import defpackage.mimetronome.base.BaseHolder
import defpackage.mimetronome.extension.inflate
import kotlinx.android.synthetic.main.item_device.view.*

class MiAdapter(listener: Listener<BluetoothDevice>) : BaseAdapter<BluetoothDevice>(listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_device))
    }

    inner class ViewHolder(itemView: View) : BaseHolder<BluetoothDevice>(itemView) {

        private val button: RadioButton = itemView.rb_device

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val item = items[position]
                reference?.get()?.onItemClick(position, item)
                miMac = item.address
                notifyDataSetChanged()
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindItem(position: Int, item: BluetoothDevice) {
            button.text = item.name
            button.setCheckedManually(item.address == miMac)
        }
    }
}