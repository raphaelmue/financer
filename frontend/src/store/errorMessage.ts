import i18next                from 'i18next';
import {Dispatch}             from 'redux';
import {notification}         from 'antd';
import store                  from './store';
import {UserActionDefinition} from './actions/user.actions';

export enum MessageType {
    UNKNOWN_ERROR = 'ErrorMessage.UnknownError',
    SERVER_UNAVAILABLE = 'ErrorMessage.ServerUnavailable',
    NOT_FOUND = 'ErrorMessage.NotFound',
    UNAUTHORIZED_OPERATION = 'ErrorMessage.UnauthorizedOperation'
}

interface ErrorMessageDefinition {
    message: string;
    timestamp: Date;
}

export class ErrorMessage implements ErrorMessageDefinition {

    message: string;
    timestamp: Date;

    constructor(message: string, timestamp: Date) {
        this.message = message;
        this.timestamp = timestamp;
    }

    static resolveError(promise: Promise<any>, action: string, dispatch: Dispatch): void {
        promise.catch((reason: any) => {
            const errorMessage = ErrorMessage.createErrorMessage(reason);
            dispatch({
                type: action,
                payload: errorMessage
            });
            notification.error({
                message: 'Error',
                description: errorMessage.message
            });
            if (errorMessage.message === i18next.t(MessageType.UNAUTHORIZED_OPERATION)) {
                store.dispatch({type: UserActionDefinition.LOGOUT_SUCCESS});
            }
        });
    }

    static createErrorMessage(reason: any, language = 'en'): ErrorMessage {
        const timestamp = new Date();
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
