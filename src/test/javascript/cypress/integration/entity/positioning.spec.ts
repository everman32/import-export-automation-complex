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

describe('Positioning e2e test', () => {
  const positioningPageUrl = '/positioning';
  const positioningPageUrlPattern = new RegExp('/positioning(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const positioningSample = { latitude: -27, longitude: 83 };

  let positioning: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/positionings+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/positionings').as('postEntityRequest');
    cy.intercept('DELETE', '/api/positionings/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (positioning) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/positionings/${positioning.id}`,
      }).then(() => {
        positioning = undefined;
      });
    }
  });

  it('Positionings menu should load Positionings page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('positioning');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Positioning').should('exist');
    cy.url().should('match', positioningPageUrlPattern);
  });

  describe('Positioning page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(positioningPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Positioning page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/positioning/new$'));
        cy.getEntityCreateUpdateHeading('Positioning');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', positioningPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/positionings',
          body: positioningSample,
        }).then(({ body }) => {
          positioning = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/positionings+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/positionings?page=0&size=20>; rel="last",<http://localhost/api/positionings?page=0&size=20>; rel="first"',
              },
              body: [positioning],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(positioningPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Positioning page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('positioning');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', positioningPageUrlPattern);
      });

      it('edit button click should load edit Positioning page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Positioning');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', positioningPageUrlPattern);
      });

      it('last delete button click should delete instance of Positioning', () => {
        cy.intercept('GET', '/api/positionings/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('positioning').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', positioningPageUrlPattern);

        positioning = undefined;
      });
    });
  });

  describe('new Positioning page', () => {
    beforeEach(() => {
      cy.visit(`${positioningPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Positioning');
    });

    it('should create an instance of Positioning', () => {
      cy.get(`[data-cy="latitude"]`).type('81').should('have.value', '81');

      cy.get(`[data-cy="longitude"]`).type('-78').should('have.value', '-78');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        positioning = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', positioningPageUrlPattern);
    });
  });
});
