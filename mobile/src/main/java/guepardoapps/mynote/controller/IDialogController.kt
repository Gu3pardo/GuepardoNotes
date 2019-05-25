package guepardoapps.mynote.controller

internal interface IDialogController {
    fun createDialog(title: String, okText: String, okCallback: () -> Unit, cancelText: String, cancelCallback: () -> Unit)
}