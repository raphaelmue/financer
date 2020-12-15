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
            method: 'PUT',
            url: TestUtil.getServerBaseUrl() + '/fixedTransactions'
        }, {fixture: 'fixed-transaction.json'}).as('createFixedTransaction');
    });

    it('should display categories and their fixed transactions', () => {
        cy.visit('/#/transactions/fixed');
        cy.wait('@getCategories');

        cy.get('.ant-tree-node-content-wrapper[title="Test Category"]').click();
        cy.wait('@getFixedTransactions');

        cy.get('.ant-list-item').should('exist');
    });

    it('should create new fixed transaction', () => {
        cy.visit('/#/transactions/fixed/create');

        cy.get('#submitStepsButton').should('be.disabled');

        cy.fillFixedTransactionData(false);
        cy.get('#nextStepButton').click();
        cy.get('#submitStepsButton').should('not.be.disabled');
        cy.get('#newVariableTransactionButton').should('be.disabled');

        cy.get('#submitStepsButton').click();
        cy.wait('@createFixedTransaction');
    });

    it('should create new fixed transaction with variable amounts', () => {
        cy.visit('/#/transactions/fixed/create');

        cy.get('#submitStepsButton').should('be.disabled');

        cy.fillFixedTransactionData(true);
        cy.get('#nextStepButton').click();
        cy.get('#submitStepsButton').should('not.be.disabled');
        cy.get('#newVariableTransactionButton').should('not.be.disabled');

        cy.get('#newVariableTransactionButton').click();
        cy.shouldDisplayDialog()
            .fillFixedTransactionAmountData()
            .submitDialog();

        cy.get('tr[data-row-key=-1]').should('exist');

        cy.get('#submitStepsButton').click();
        cy.wait('@createFixedTransaction');
    });
});

export {};
