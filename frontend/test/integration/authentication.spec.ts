describe('Authentication Component Test', () => {
    it('should login correctly with email and password', () => {

        cy.intercept({
            method: 'GET',
            url: 'http://localhost:3001/api/1.0-SNAPSHOT/users',
            query: {
                email: 'test@gmail.com',
                password: 'password'
            },
            headers: {}
        }, {fixture: 'user.json'}).as('loginUser');

        cy.visit('/');

        cy.location('href').should('contain', '/authentication');
        cy.get('#login_email').type('test@gmail.com');
        cy.get('#login_password').type('password{enter}');

        cy.wait('@loginUser');

        cy.get('.ant-page-header-heading-title')
            .should('exist')
            .and('contain.text', 'Dashboard');

        cy.location('href').should('contain', '/dashboard');
    });
});

export {};
