import TestUtil from '../util/util';
import '../support';


describe('Profile Test', () => {

    beforeEach(() => {
        cy.login();

        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/users/1/categories'
        }, {fixture: 'categories.json', delayMs: 100}).as('getCategories');
    });

    it('should display user details, categories and tokens', () => {
        cy.visit('/#/profile/');

        cy.wait('@getCategories');
        cy.get('span[title="Test Category"]').should('exist').and('be.visible');

        cy.get('#rc-tabs-0-tab-devicesTab').click();
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

    it('should create a new category', () => {
        cy.intercept({
            method: 'PUT',
            url: TestUtil.getServerBaseUrl() + '/categories'
        }, {fixture: 'category.json'}).as('createCategory');

        cy.visit('/#/profile/');

        cy.wait('@getCategories');
        cy.get('span[title="Test Category"]').click();
        cy.get('#createCategoryButton').click();

        cy.shouldDisplayDialog();
        cy.get('#categoryDialog_categoryName').type('Another Test Category');
        cy.submitDialog()
            .wait('@createCategory')
            .shouldDisplayNotification();

        cy.get('.anticon-plus-square.ant-tree-switcher-line-icon').click();
        cy.get('span[title="Another Test Category"]').should('exist').and('be.visible');
    });

    it('should update a category', () => {
        cy.intercept({
            method: 'POST',
            url: TestUtil.getServerBaseUrl() + '/categories/1'
        }, {fixture: 'category.json'}).as('updateCategory');

        cy.visit('/#/profile/');

        cy.wait('@getCategories');
        cy.get('span[title="Test Category"]').click();
        cy.get('#editCategoryButton').click();

        cy.shouldDisplayDialog();
        cy.get('#categoryDialog_categoryName').type('{movetostart}Another ');
        cy.submitDialog()
            .wait('@updateCategory')
            .shouldDisplayNotification();

        cy.get('.anticon-plus-square.ant-tree-switcher-line-icon').click();
        cy.get('span[title="Another Test Category"]').should('exist').and('be.visible');
    });

    it.only('should delete a category', () => {
        cy.intercept({
            method: 'DELETE',
            url: TestUtil.getServerBaseUrl() + '/categories/1'
        }, {fixture: 'category.json'}).as('deleteCategory');

        cy.visit('/#/profile/');

        cy.wait('@getCategories');
        cy.get('span[title="Test Category"]').click();
        cy.get('#deleteCategoryButton').click();

        cy.shouldDisplayDialog()
            .submitConfirmDialog()
            .wait('@deleteCategory')
            .shouldDisplayNotification();
    });
});
