import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('StatementType e2e test', () => {
  const statementTypePageUrl = '/statement-type';
  const statementTypePageUrlPattern = new RegExp('/statement-type(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const statementTypeSample = { name: 'bypass optimizing monitor' };

  let statementType: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/statement-types+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/statement-types').as('postEntityRequest');
    cy.intercept('DELETE', '/api/statement-types/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (statementType) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/statement-types/${statementType.id}`,
      }).then(() => {
        statementType = undefined;
      });
    }
  });

  it('StatementTypes menu should load StatementTypes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('statement-type');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StatementType').should('exist');
    cy.url().should('match', statementTypePageUrlPattern);
  });

  describe('StatementType page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(statementTypePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StatementType page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/statement-type/new$'));
        cy.getEntityCreateUpdateHeading('StatementType');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', statementTypePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/statement-types',
          body: statementTypeSample,
        }).then(({ body }) => {
          statementType = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/statement-types+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/statement-types?page=0&size=20>; rel="last",<http://localhost/api/statement-types?page=0&size=20>; rel="first"',
              },
              body: [statementType],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(statementTypePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details StatementType page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('statementType');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', statementTypePageUrlPattern);
      });

      it('edit button click should load edit StatementType page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StatementType');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', statementTypePageUrlPattern);
      });

      it('last delete button click should delete instance of StatementType', () => {
        cy.intercept('GET', '/api/statement-types/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('statementType').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', statementTypePageUrlPattern);

        statementType = undefined;
      });
    });
  });

  describe('new StatementType page', () => {
    beforeEach(() => {
      cy.visit(`${statementTypePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StatementType');
    });

    it('should create an instance of StatementType', () => {
      cy.get(`[data-cy="name"]`).type('Checking Paradigm Ball').should('have.value', 'Checking Paradigm Ball');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        statementType = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', statementTypePageUrlPattern);
    });
  });
});
