export interface DataDialog<Data> {
    visible: boolean,
    data?: Data,
    onSubmit?: (data: Data) => void,
    onCancel?: () => void
}
