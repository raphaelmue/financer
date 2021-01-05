export interface DataDialog<Data> {
    visible: boolean,
    data?: Data,
    onSubmit?: (data: Data) => Promise<void> | void,
    onCancel?: () => void
}
