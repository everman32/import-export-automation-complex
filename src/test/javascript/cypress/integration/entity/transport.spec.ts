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

describe('Transport e2e test', () => {
  const transportPageUrl = '/transport';
  const transportPageUrlPattern = new RegExp('/transport(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const transportSample = { brand: 'extranet', model: 'Hat Consultant', vin: 'invoiceXXXXXXXXXX' };

  let transport: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/transports+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/transports').as('postEntityRequest');
    cy.intercept('DELETE', '/api/transports/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (transport) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/transports/${transport.id}`,
      }).then(() => {
        transport = undefined;
      });
    }
  });

  it('Transports menu should load Transports page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('transport');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Transport').should('exist');
    cy.url().should('match', transportPageUrlPattern);
  });

  describe('Transport page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(transportPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Transport page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/transport/new$'));
        cy.getEntityCreateUpdateHeading('Transport');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', transportPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/transports',
          body: transportSample,
        }).then(({ body }) => {
          transport = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/transports+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/transports?page=0&size=20>; rel="last",<http://localhost/api/transports?page=0&size=20>; rel="first"',
              },
              body: [transport],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(transportPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Transport page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('transport');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', transportPageUrlPattern);
      });

      it('edit button click should load edit Transport page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Transport');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', transportPageUrlPattern);
      });

      it('last delete button click should delete instance of Transport', () => {
        cy.intercept('GET', '/api/transports/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('transport').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', transportPageUrlPattern);

        transport = undefined;
      });
    });
  });

  describe('new Transport page', () => {
    beforeEach(() => {
      cy.visit(`${transportPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Transport');
    });

    it('should create an instance of Transport', () => {
      cy.get(`[data-cy="brand"]`).type('calculate communities RAM').should('have.value', 'calculate communities RAM');

      cy.get(`[data-cy="model"]`).type('AGP Chips').should('have.value', 'AGP Chips');

      cy.get(`[data-cy="vin"]`).type('Guarani throughpu').should('have.value', 'Guarani throughpu');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        transport = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', transportPageUrlPattern);
    });
  });
});
