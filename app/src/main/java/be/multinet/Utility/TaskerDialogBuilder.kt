import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes

/**
 * This class builds [AlertDialog]s
 */
class TaskerDialogBuilder {

    companion object {
        /**
         * Build an [AlertDialog] with the given options
         * @param ctx the context for the [AlertDialog.Builder]
         * @param title the [AlertDialog]'s title
         * @param message the [AlertDialog]'s message
         * @param onOkPressed the callback for the positive button
         * @param onCancelPressed the callback for the negative button
         * @param cancelBtnText the text for the negative button
         * @param okBtnText the text for the positive button
         */
        fun buildDialog(ctx: Context,
                        title: CharSequence,
                        @StringRes message: Int,
                        onOkPressed: DialogInterface.OnClickListener,
                        @StringRes okBtnText: Int,
                        onCancelPressed: DialogInterface.OnClickListener,
                        @StringRes cancelBtnText: Int
        ) : AlertDialog {
            val builder = AlertDialog.Builder(ctx)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton(okBtnText,onOkPressed)
            builder.setNegativeButton(cancelBtnText,onCancelPressed)
            return builder.create()
        }
    }
}