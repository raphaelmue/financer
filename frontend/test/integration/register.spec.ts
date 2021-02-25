import TestUtil from '../util/util';

describe('Registration Test', () => {
    it('should register user', () => {
        cy.intercept({
            method: 'PUT',
            url: TestUtil.getServerBaseUrl() + '/users',
        }, {fixture: 'user.json'}).as('registerUser');

        cy.visit('/');

        cy.location('href').should('contain', '/landing');
        cy.get('#rc-tabs-0-tab-register').click();
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
