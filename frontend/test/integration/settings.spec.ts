import TestUtil from '../util/util';


describe('Settings Test', () => {
    beforeEach(() => {
        cy.login();

        cy.intercept({
            method: 'POST',
            url: TestUtil.getServerBaseUrl() + '/users/1/settings'
        }, {fixture: 'user.json'}).as('updateSettings');
    });

    it('should update users settings', () => {
        cy.visit('/#/settings');

        cy.get('#settings_language').focus().type('Deutsch{enter}');
        cy.wait('@updateSettings')
            .submitConfirmDialog();

        cy.get('#settings_currency').focus().type('USD{enter}');
        cy.wait('@updateSettings');

        cy.get('#settings_changeAmountSignAutomatically').click();
        cy.wait('@updateSettings');

        cy.get('#settings_theme').focus().type('Dark Theme{enter}');
        cy.wait('@updateSettings').submitConfirmDialog();
    })
});
