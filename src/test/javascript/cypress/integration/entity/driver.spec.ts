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

describe('Driver e2e test', () => {
  const driverPageUrl = '/driver';
  const driverPageUrlPattern = new RegExp('/driver(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const driverSample = { firstname: 'Fantastic', lastname: 'sexy Virginia', phone: '(737) 672-7324', experience: 33609 };

  let driver: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/drivers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/drivers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/drivers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (driver) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/drivers/${driver.id}`,
      }).then(() => {
        driver = undefined;
      });
    }
  });

  it('Drivers menu should load Drivers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('driver');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Driver').should('exist');
    cy.url().should('match', driverPageUrlPattern);
  });

  describe('Driver page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(driverPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Driver page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/driver/new$'));
        cy.getEntityCreateUpdateHeading('Driver');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', driverPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/drivers',
          body: driverSample,
        }).then(({ body }) => {
          driver = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/drivers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/drivers?page=0&size=20>; rel="last",<http://localhost/api/drivers?page=0&size=20>; rel="first"',
              },
              body: [driver],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(driverPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Driver page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('driver');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', driverPageUrlPattern);
      });

      it('edit button click should load edit Driver page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Driver');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', driverPageUrlPattern);
      });

      it('last delete button click should delete instance of Driver', () => {
        cy.intercept('GET', '/api/drivers/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('driver').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', driverPageUrlPattern);

        driver = undefined;
      });
    });
  });

  describe('new Driver page', () => {
    beforeEach(() => {
      cy.visit(`${driverPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Driver');
    });

    it('should create an instance of Driver', () => {
      cy.get(`[data-cy="firstname"]`).type('XSS dot-com').should('have.value', 'XSS dot-com');

      cy.get(`[data-cy="patronymic"]`).type('Outdoors Home turquoise').should('have.value', 'Outdoors Home turquoise');

      cy.get(`[data-cy="lastname"]`).type('Avon').should('have.value', 'Avon');

      cy.get(`[data-cy="phone"]`).type('457-242-4007 x46452').should('have.value', '457-242-4007 x46452');

      cy.get(`[data-cy="experience"]`).type('65041').should('have.value', '65041');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        driver = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', driverPageUrlPattern);
    });
  });
});
