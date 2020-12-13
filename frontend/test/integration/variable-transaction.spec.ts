import TestUtil from '../util/util';
import '../support';


describe('Variable Transaction Test', () => {
    beforeEach(() => {
        cy.login();

        // get all transactions
        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/users/1/variableTransactions',
            query: {
                page: '0',
                size: '20'
            },
        }, {fixture: 'variable-transactions.json'}).as('getVariableTransactions');

        // get single transaction
        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/variableTransactions/1',
        }, {fixture: 'variable-transaction.json'}).as('getVariableTransaction');

        // get all categories
        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/users/1/categories'
        }, {fixture: 'categories.json'}).as('getCategories');
    });

    it('should display list of variable transactions', () => {
        cy.visit('/#/transactions/variable');
        cy.wait('@getVariableTransactions');
        cy.get('tr[data-row-key=1]').should('exist');
    });

    it('should display variable transaction details', () => {
        cy.visit('/#/transactions/variable');
        cy.wait('@getVariableTransactions');

        cy.get('tr[data-row-key=1]').click();

        cy.location('href').should('contain', '/transactions/variable/1');
        cy.wait('@getVariableTransaction');

        // assert product table exists
        cy.get('tr[data-row-key=1]').should('exist');
    });

    it('should create variable transaction', () => {
        cy.intercept({
            method: 'PUT',
            url: TestUtil.getServerBaseUrl() + '/variableTransactions',
        }, {fixture: 'variable-transaction.json', delayMs: 100}).as('createVariableTransaction');

        cy.visit('/#/transactions/variable');
        cy.wait('@getVariableTransactions');

        cy.get('#createVariableTransactionButton').click();
        cy.get('#submitStepsButton').should('be.disabled');
        cy.fillVariableTransactionData();

        cy.get('#nextStepButton').click();
        cy.get('#createProductButton').click();

        cy.shouldDisplayDialog()
            .fillProductData()
            .submitDialog()
            .shouldNotDisplayDialog();

        cy.get('#submitStepsButton').should('not.be.disabled');
        cy.get('#submitStepsButton').click();
        cy.wait('@createVariableTransaction');

        cy.location('href').should('contain', '/transactions/variable');
        cy.location('href').should('not.contain', '/1');
    });

    it('should update variable transaction', () => {
        cy.intercept({
            method: 'POST',
            url: TestUtil.getServerBaseUrl() + '/variableTransactions/1',
        }, {fixture: 'variable-transaction.json', delayMs: 100}).as('updateVariableTransaction');

        cy.visit('/#/transactions/variable/1');
        cy.wait('@getVariableTransaction');

        cy.get('#editVariableTransaction').click();
        cy.shouldDisplayDialog()
            .fillVariableTransactionData()
            .submitDialog()
            .shouldDisplayNotification();
    });

    it('should delete variable transaction', () => {
        cy.intercept({
            method: 'DELETE',
            url: TestUtil.getServerBaseUrl() + '/variableTransactions/1',
        }, {delayMs: 100}).as('deleteVariableTransaction');

        cy.visit('/#/transactions/variable/1');
        cy.wait('@getVariableTransaction');

        cy.get('#deleteVariableTransaction').click();

        cy.shouldDisplayDialog()
            .submitConfirmDialog()
            .shouldDisplayNotification()
            .shouldNotDisplayDialog();

        cy.location('href').should('contain', '/transactions/variable');
        cy.location('href').should('not.contain', '/1');
    });

    it('should add product to variable transaction', () => {
        cy.intercept({
            method: 'PUT',
            url: TestUtil.getServerBaseUrl() + '/variableTransactions/1/products',
        }, {fixture: 'product.json', delayMs: 100}).as('createProduct');

        cy.visit('/#/transactions/variable/1');
        cy.wait('@getVariableTransaction');

        cy.get('#createProductButton').click();
        cy.shouldDisplayDialog()
            .fillProductData()
            .submitDialog()
            .wait('@createProduct')
            .shouldNotDisplayDialog()
            .shouldDisplayNotification();

        cy.get('tr[data-row-key=2]').should('exist');
    });

    it('should delete product from variable transaction', () => {
        cy.intercept({
            method: 'DELETE',
            url: TestUtil.getServerBaseUrl() + '/variableTransactions/1/products',
            query: {
                productIds: '1'
            }
        }, {delayMs: 100}).as('deleteProduct');

        cy.visit('/#/transactions/variable/1');
        cy.wait('@getVariableTransaction');

        cy.get('tr[data-row-key=1] input[type=checkbox]').click();
        cy.get('#deleteProductButton').should('be.visible');
        cy.get('#deleteProductButton').click();

        cy.shouldDisplayDialog()
            .submitConfirmDialog()
            .wait('@deleteProduct')
            .shouldNotDisplayDialog()
            .shouldDisplayNotification();
        cy.get('tr[data-row-key=1]').should('not.exist');
    });
});

export {};
