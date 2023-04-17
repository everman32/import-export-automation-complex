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

describe('Trip e2e test', () => {
  const tripPageUrl = '/trip';
  const tripPageUrlPattern = new RegExp('/trip(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const tripSample = { authorizedCapital: 29679, threshold: 54031 };

  let trip: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trips+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trips').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trips/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trip) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trips/${trip.id}`,
      }).then(() => {
        trip = undefined;
      });
    }
  });

  it('Trips menu should load Trips page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trip');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Trip').should('exist');
    cy.url().should('match', tripPageUrlPattern);
  });

  describe('Trip page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(tripPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Trip page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trip/new$'));
        cy.getEntityCreateUpdateHeading('Trip');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', tripPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trips',
          body: tripSample,
        }).then(({ body }) => {
          trip = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trips+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trips?page=0&size=20>; rel="last",<http://localhost/api/trips?page=0&size=20>; rel="first"',
              },
              body: [trip],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(tripPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Trip page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trip');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', tripPageUrlPattern);
      });

      it('edit button click should load edit Trip page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Trip');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', tripPageUrlPattern);
      });

      it('last delete button click should delete instance of Trip', () => {
        cy.intercept('GET', '/api/trips/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('trip').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', tripPageUrlPattern);

        trip = undefined;
      });
    });
  });

  describe('new Trip page', () => {
    beforeEach(() => {
      cy.visit(`${tripPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Trip');
    });

    it('should create an instance of Trip', () => {
      cy.get(`[data-cy="authorizedCapital"]`).type('48825').should('have.value', '48825');

      cy.get(`[data-cy="threshold"]`).type('43669').should('have.value', '43669');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        trip = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', tripPageUrlPattern);
    });
  });
});
