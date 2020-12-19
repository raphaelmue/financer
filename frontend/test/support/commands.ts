// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

Cypress.Commands.add('login', () => {
    cy.fixture('redux-state.json').then((state) => {
        cy.fixture('user.json').then((user) => {
            state.user = {
                'isLoading': false,
                'user': user
            };
            window.localStorage.setItem('reduxState', JSON.stringify(state));
        });
    });
});

Cypress.Commands.add('fillVariableTransactionData', () => {
    cy.fixture('variable-transaction.json').then((variableTransaction) => {
        cy.get('.ant-tree-select').click();
        cy.wait('@getCategories');
        cy.get('.ant-select-tree-node-content-wrapper[title="Test Category"]').click();

        cy.get('#variableTransactionDataForm_valueDate').click();
        cy.get('.ant-picker-today-btn').should('be.visible');
        cy.get('.ant-picker-today-btn').click();

        cy.get('#variableTransactionDataForm_vendor').type('{selectAll}{backspace}' + variableTransaction.vendor);
        cy.get('#variableTransactionDataForm_description').type('{selectAll}{backspace}' + variableTransaction.description);
    });
});

Cypress.Commands.add('fillProductData', () => {
    cy.get('input[name=productName]').type('Test Product 2');
    cy.get('input[name=quantity]').type('{backspace}10');
    cy.get('input[name=amount]').type('{selectall}{backspace}20');
});

Cypress.Commands.add('fillFixedTransactionData', (hasVariableAmounts: boolean) => {
    cy.fixture('fixed-transaction.json').then((fixedTransaction) => {
        cy.get('.ant-picker-clear').click();
        cy.get('#fixedTransactionDataForm_valueDate').click().type(fixedTransaction.timeRange.startDate + '{enter}{enter}');

        cy.get('.ant-tree-select').click();
        cy.wait('@getCategories');
        cy.get('.ant-select-tree-node-content-wrapper[title="Test Category"]').click();

        cy.get('#fixedTransactionDataForm_product').type(fixedTransaction.product);

        cy.get('#fixedTransactionDataForm_amount').should('be.enabled');
        cy.get('#fixedTransactionDataForm_amount').should('be.enabled');

        if (hasVariableAmounts) {
            cy.get('#fixedTransactionDataForm_hasVariableAmounts').click();
            cy.get('#fixedTransactionDataForm_amount').should('be.disabled');
            cy.get('#fixedTransactionDataForm_day').should('be.disabled');
        } else {
            cy.get('#fixedTransactionDataForm_amount').type(fixedTransaction.amount.amount);
            cy.get('#fixedTransactionDataForm_day').type(fixedTransaction.day);
        }

        cy.get('#fixedTransactionDataForm_vendor').type(fixedTransaction.vendor);
        cy.get('#fixedTransactionDataForm_description').type(fixedTransaction.description);
    });
});

Cypress.Commands.add('fillFixedTransactionAmountData', () => {
    cy.get('#fixedTransactionDialog_valueDate').click();
    cy.get('.ant-picker-today-btn').should('be.visible');
    cy.get('.ant-picker-today-btn').click();

    cy.get('input[name=amount]:visible').type('{selectall}{backspace}20');
});

Cypress.Commands.add('shouldDisplayDialog', () => {
    cy.get('.ant-modal-content').should('exist').and('be.visible');
});

Cypress.Commands.add('shouldNotDisplayDialog', () => {
    cy.get('.ant-modal-content').should('not.exist');
});

Cypress.Commands.add('shouldDisplayNotification', () => {
    cy.get('.ant-notification-notice.ant-notification-notice-closable').should('be.visible');
});

Cypress.Commands.add('submitDialog', () => {
    cy.get('.ant-modal-footer .ant-btn-primary').click();
});

Cypress.Commands.add('submitConfirmDialog', () => {
    cy.get('.ant-modal-confirm-btns .ant-btn-primary span').click();
});

declare namespace Cypress {
    interface Chainable<Subject = any> {
        login(): Chainable<Element>,

        interceptGetCategories(): Chainable<Element>,

        fillVariableTransactionData(): Chainable<Element>,

        fillProductData(): Chainable<Element>

        fillFixedTransactionData(hasVariableAmounts: boolean): Chainable<Element>,

        fillFixedTransactionAmountData(): Chainable<Element>,

        shouldDisplayDialog(): Chainable<Element>,

        shouldNotDisplayDialog(): Chainable<Element>,

        shouldDisplayNotification(): Chainable<Element>,

        submitDialog(): Chainable<Element>,

        submitConfirmDialog(): Chainable<Element>
    }
}
