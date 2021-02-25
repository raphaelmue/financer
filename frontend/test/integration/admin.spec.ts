import TestUtil from '../util/util';

describe('Administration Test', () => {
    beforeEach(() => {
        cy.login();
    });

    it('should update admin settings', () => {
        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/admin/configuration'
        }, {fixture: 'admin-configuration.json'}).as('getAdminConfiguration');

        cy.intercept({
            method: 'POST',
            url: TestUtil.getServerBaseUrl() + '/admin/configuration'
        }, {fixture: 'admin-configuration.json'}).as('updateAdminConfiguration');

        cy.visit('/#/admin/configuration');

        cy.wait('@getAdminConfiguration');

        cy.get('#defaultLanguage').focus().type('Deutsch{enter}');
        cy.wait('@updateAdminConfiguration')
            .shouldDisplayNotification();

        cy.get('#defaultCurrency').focus().type('EUR{enter}');
        cy.wait('@updateAdminConfiguration')
            .shouldDisplayNotification();
    });

    it('should display list of users', () => {
        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/admin/users'
        }, {fixture: 'users.json'}).as('getUsers');

        cy.visit('/#/admin/users');

        cy.wait('@getUsers');
        cy.get('tr[data-row-key=1]').should('exist');

        cy.get('tr[data-row-key=1]').click();
        cy.location('href').should('contain', '/profile/1');
    });
});

