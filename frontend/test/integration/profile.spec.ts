import TestUtil from '../util/util';
import '../support';


describe('Profile Test', () => {

    beforeEach(() => {
        cy.login();
    });

    it('should display user details and tokens', () => {
        cy.visit('/#/profile/');
        cy.get('tr[data-row-key=1]').should('exist');
    });

    it('should update users personal information', () => {
        cy.intercept({
            method: 'POST',
            url: TestUtil.getServerBaseUrl() + '/users/1/personalInformation'
        }, {fixture: 'user.json', delayMs: 100}).as('updateUsersData');

        cy.visit('/#/profile/');
        cy.get('#updateProfileButton').click();

        cy.shouldDisplayDialog()
            .submitDialog()
            .wait('@updateUsersData')
            .shouldDisplayNotification();
    });

    it('should update users password', () => {
        cy.intercept({
            method: 'POST',
            url: TestUtil.getServerBaseUrl() + '/users/1/password'
        }, {fixture: 'user.json', delayMs: 100}).as('updateUsersPassword');

        cy.visit('/#/profile/');
        cy.get('#updatePasswordButton').click();

        cy.shouldDisplayDialog();
        cy.get('#updatePassword_currentPassword').type('password');
        cy.get('#updatePassword_password').type('newPassword');
        cy.get('#updatePassword_repeatPassword').type('newPassword');

        cy.submitDialog()
            .wait('@updateUsersPassword')
            .shouldDisplayNotification();
    });
});
