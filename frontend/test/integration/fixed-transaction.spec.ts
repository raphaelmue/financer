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
    });

    it('should display categories and their fixed transactions', () => {
        cy.visit('/#/transactions/fixed');
        cy.wait('@getCategories');

        cy.get('.ant-tree-node-content-wrapper[title="Test Category"]').click();
        cy.wait('@getFixedTransactions');

        cy.get('.ant-list-item').should('exist');
    });

    // it('should create new fixed transaction', () => {
    //     cy.visit('/#/transactions/fixed');
    //     cy.wait('@getCategories');
    //
    //     cy.get('#newFixedTransactionButton').click();
    //     cy.location('href').should('contain', '/transactions/fixed/create');
    //     cy.get('#submitStepsButton').should('be.disabled');
    //
    //     cy.fillFixedTransactionData();
    //     cy.get('#submitStepsButton').should('be.disabled');
    //     cy.get('#nextStepButton').click();
    // });
});
