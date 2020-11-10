import i18next    from 'i18next';
import {Dispatch} from 'redux';

export enum MessageType {
    UNKNOWN_ERROR = 'ErrorMessage.UnknownError',
    SERVER_UNAVAILABLE = 'ErrorMessage.ServerUnavailable',
    NOT_FOUND = 'ErrorMessage.NotFound',
    UNAUTHORIZED_OPERATION = 'ErrorMessage.UnauthorizedOperation'
}

interface ErrorMessageDefinition {
    message: String;
    timestamp: Date;
}

export class ErrorMessage implements ErrorMessageDefinition {

    message: String;
    timestamp: Date;

    constructor(message: String, timestamp: Date) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public static resolveError(promise: Promise<any>, action: string, dispatch: Dispatch) {
        promise.catch((reason: any) => dispatch({
            type: action,
            payload: ErrorMessage.createErrorMessage(reason)
        }));
    }

    public static createErrorMessage(reason: any, language: string = 'en'): ErrorMessage {
        let timestamp = new Date();
        let message: MessageType = MessageType.UNKNOWN_ERROR;
        if (reason === 'TypeError: Failed to fetch') {
            message = MessageType.SERVER_UNAVAILABLE;
        } else {
            switch (reason.status) {
                case 400:
                    message = MessageType.NOT_FOUND;
                    break;
                case 403:
                    message = MessageType.UNAUTHORIZED_OPERATION;
                    break;
            }
        }

        return new ErrorMessage(i18next.t(message, {lng: language}), timestamp);
    }
}
