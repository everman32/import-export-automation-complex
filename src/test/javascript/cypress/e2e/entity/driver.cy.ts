import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Driver e2e test', () => {
  const driverPageUrl = '/driver';
  const driverPageUrlPattern = new RegExp('/driver(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const driverSample = { firstname: 'rotating', lastname: 'deploy whereas deceivingly', phone: '930-989-8844 x857', experience: 13673.36 };

  let driver;

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
      if (response?.body.length === 0) {
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
          expect(response?.statusCode).to.equal(200);
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
            },
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
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', driverPageUrlPattern);
      });

      it('edit button click should load edit Driver page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Driver');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', driverPageUrlPattern);
      });

      it('edit button click should load edit Driver page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Driver');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
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
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
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
      cy.get(`[data-cy="firstname"]`).type('yowza unless woot');
      cy.get(`[data-cy="firstname"]`).should('have.value', 'yowza unless woot');

      cy.get(`[data-cy="patronymic"]`).type('splosh attend ugh');
      cy.get(`[data-cy="patronymic"]`).should('have.value', 'splosh attend ugh');

      cy.get(`[data-cy="lastname"]`).type('soybean duh sham');
      cy.get(`[data-cy="lastname"]`).should('have.value', 'soybean duh sham');

      cy.get(`[data-cy="phone"]`).type('210-490-5413 x372');
      cy.get(`[data-cy="phone"]`).should('have.value', '210-490-5413 x372');

      cy.get(`[data-cy="experience"]`).type('423.5');
      cy.get(`[data-cy="experience"]`).should('have.value', '423.5');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        driver = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', driverPageUrlPattern);
    });
  });
});
