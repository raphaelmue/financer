import TestUtil from '../util/util';
import '../support';

describe('Fixed Transaction Test', () => {
    beforeEach(() => {
        cy.login();

        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/users/1/categories'
        }, {fixture: 'categories.json', delayMs: 100}).as('getCategories');

        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/users/1/fixedTransactions',
            query: {
                onlyActive: 'false',
                categoryId: '2'
            }
        }, {fixture: 'fixed-transactions.json', delayMs: 100}).as('getFixedTransactions');

        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/fixedTransactions/52'
        }, {fixture: 'fixed-transaction.json'}).as('getFixedTransaction');
    });

    it('should display categories and their fixed transactions', () => {
        cy.visit('/#/transactions/fixed');
        cy.wait('@getCategories');

        cy.get('.ant-tree-node-content-wrapper[title="Test Category 2"]').click();
        cy.wait('@getFixedTransactions');

        cy.get('.ant-list-item').should('exist');
    });

    it('should display fixed transaction details', () => {
        cy.visit('/#/transactions/fixed/52');
        cy.wait('@getFixedTransaction');

        cy.get('tr[data-row-key=2]').should('exist');
    });

    it('should create new fixed transaction', () => {
        cy.intercept({
            method: 'PUT',
            url: TestUtil.getServerBaseUrl() + '/fixedTransactions'
        }, {fixture: 'fixed-transaction.json'}).as('createFixedTransaction');

        cy.visit('/#/transactions/fixed/create');

        cy.get('#submitStepsButton').should('be.disabled');

        cy.fillFixedTransactionData(false);
        cy.get('#nextStepButton').click();
        cy.get('#submitStepsButton').should('not.be.disabled');
        cy.get('#newFixedTransactionAmountButton').should('be.disabled');

        cy.get('#submitStepsButton').click();
        cy.wait('@createFixedTransaction');
    });

    it('should create new fixed transaction with variable amounts', () => {
        cy.intercept({
            method: 'PUT',
            url: TestUtil.getServerBaseUrl() + '/fixedTransactions'
        }, {fixture: 'fixed-transaction.json'}).as('createFixedTransaction');

        cy.visit('/#/transactions/fixed/create');

        cy.get('#submitStepsButton').should('be.disabled');

        cy.fillFixedTransactionData(true);
        cy.get('#nextStepButton').click();
        cy.get('#submitStepsButton').should('not.be.disabled');
        cy.get('#newFixedTransactionAmountButton').should('not.be.disabled');

        cy.get('#newFixedTransactionAmountButton').click();
        cy.shouldDisplayDialog()
            .fillFixedTransactionAmountData()
            .submitDialog();

        cy.get('tr[data-row-key=-1]').should('exist');

        cy.get('#submitStepsButton').click();
        cy.wait('@createFixedTransaction');
    });

    it('should update fixed transaction', () => {
        cy.intercept({
            method: 'POST',
            url: TestUtil.getServerBaseUrl() + '/fixedTransactions/52'
        }, {fixture: 'fixed-transaction.json'}).as('updateFixedTransaction');

        cy.visit('/#/transactions/fixed/52');
        cy.wait('@getFixedTransaction');

        cy.get('#editFixedTransaction').click();
        cy.shouldDisplayDialog()
            .submitDialog()
            .wait('@updateFixedTransaction')
            .shouldDisplayNotification();
    });

    it('should delete fixed transaction', () => {
        cy.intercept({
            method: 'DELETE',
            url: TestUtil.getServerBaseUrl() + '/fixedTransactions/52'
        }, []).as('deleteFixedTransaction');

        cy.visit('/#/transactions/fixed/52');
        cy.wait('@getFixedTransaction');

        cy.get('#deleteFixedTransaction').click();
        cy.shouldDisplayDialog()
            .submitConfirmDialog()
            .wait('@deleteFixedTransaction');

        cy.location('href').should('contain', '/transactions/fixed');
        cy.location('href').should('not.contain', '/52');
    });

    it('should add fixed transaction amount to fixed transaction', () => {
        cy.intercept({
            method: 'PUT',
            url: TestUtil.getServerBaseUrl() + '/fixedTransactions/52/transactionAmounts'
        }, {fixture: 'fixed-transaction-amount.json', delayMs: 100}).as('createFixedTransactionAmount');

        cy.visit('/#/transactions/fixed/52');
        cy.wait('@getFixedTransaction');

        cy.get('#newFixedTransactionAmountButton').click();
        cy.shouldDisplayDialog()
            .fillFixedTransactionAmountData()
            .submitDialog()
            .wait('@createFixedTransactionAmount')
            .shouldNotDisplayDialog()
            .shouldDisplayNotification();

        cy.get('tr[data-row-key=50]').should('exist');
    });

    it('should delete fixed transaction amount from fixed transaction', () => {
        cy.intercept({
            method: 'DELETE',
            url: TestUtil.getServerBaseUrl() + '/fixedTransactions/52/transactionAmounts',
            query: {
                fixedTransactionAmountIds: '2'
            }
        }, []).as('deleteFixedTransactionAmounts');

        cy.visit('/#/transactions/fixed/52');
        cy.wait('@getFixedTransaction');

        cy.get('#deleteFixedTransactionAmountButton').should('not.be.visible');
        cy.get('tr[data-row-key=2] input[type=checkbox]').should('exist').click();
        cy.get('#deleteFixedTransactionAmountButton').should('be.visible').click();

        cy.wait(100);
        cy.shouldDisplayDialog()
            .submitConfirmDialog()
            // .wait('@deleteFixedTransactionAmounts')
            .shouldNotDisplayDialog()
            .shouldDisplayNotification();

        cy.get('tr[data-row-key=2]').should('not.exist');
    });
});

export {};
