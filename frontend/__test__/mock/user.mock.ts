import {GenderEnum, User}          from '../../src/.openapi/models';
import {LoginUserRequest, UserApi} from '../../src/.openapi/apis';
import {mocked}                    from 'ts-jest/utils';


jest.mock('../../src/.openapi/apis');
mocked(UserApi.prototype.loginUser).mockImplementation((data: LoginUserRequest): Promise<User> => new Promise<User>((resolve, reject) => {
    if (data.email === 'test@gmail.com' && data.password === 'password') {
        resolve(mockUser);
    } else {
        reject();
    }
}));

export const mockUser: User = {
    id: 1,
    email: {emailAddress: 'test@gmail.com'},
    name: {
        firstName: 'John',
        surname: 'Doe'
    },
    birthDate: {birthDate: new Date('2020-01-01')},
    gender: {gender: GenderEnum.MALE},
    activeToken: {
        id: 1,
        token: {
            token: 'UPOPBEZR7c9GqpGs0IFrKdgtRvUuZMsI8ubfOK33cDX6ArmtFiqxnP6ayHQFHsbu'
        },
        ipAddress: {ipAddress: '192.168.0.1'}
    },
    tokens: [
        {
            id: 1,
            token: {
                token: 'UPOPBEZR7c9GqpGs0IFrKdgtRvUuZMsI8ubfOK33cDX6ArmtFiqxnP6ayHQFHsbu'
            },
            ipAddress: {ipAddress: '192.168.0.1'}
        }
    ],
    verified: true,
    settings: {
        LANGUAGE: {
            id: 1,
            value: 'en_DE'
        },
        CURRENCY: {
            id: 1,
            value: 'EUR'
        },
    }
};
