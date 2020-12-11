import TestUtil from '../util/util';

describe('Authentication Component Test', () => {
    it('should login correctly with email and password', () => {
        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/users',
            query: {
                email: 'test@gmail.com',
                password: 'password'
            },
            headers: {}
        }, {fixture: 'user.json'}).as('loginUser');

        cy.visit('/');

        cy.location('href').should('contain', '/authentication');
        cy.get('#login_email').type('test@gmail.com');
        cy.get('#login_password').type('password{enter}');

        cy.wait('@loginUser');

        cy.get('.ant-page-header-heading-title')
            .should('exist')
            .and('contain.text', 'Dashboard');

        cy.location('href').should('contain', '/dashboard');
    });

    it('should register user', () => {
        cy.intercept({
            method: 'PUT',
            url: TestUtil.getServerBaseUrl() + '/users',
        }, {fixture: 'user.json'}).as('registerUser');

        cy.visit('/');

        cy.location('href').should('contain', '/authentication');
        cy.get('#rc-tabs-0-tab-registerTab').click();
        cy.get('#register_email').type('test@gmail.com');
        cy.get('#register_name').type('John');
        cy.get('#register_surname').type('Doe');
        cy.get('#register_birthDate').click();
        cy.get('.ant-picker-today-btn').should('be.visible');
        cy.get('.ant-picker-today-btn').click();
        cy.get('.ant-select-selection-search-input').click();
        cy.get('.ant-select-item-option[title="Male"]').should('be.visible');
        cy.get('.ant-select-item-option[title="Male"]').click();
        cy.get('#register_password').type('password');
        cy.get('#register_repeatPassword').type('password{enter}');

        cy.wait('@registerUser');

        cy.get('.ant-page-header-heading-title')
            .should('exist')
            .and('contain.text', 'Dashboard');

        cy.location('href').should('contain', '/dashboard');
    });
});

export {};
