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

describe('Statement e2e test', () => {
  const statementPageUrl = '/statement';
  const statementPageUrlPattern = new RegExp('/statement(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const statementSample = { name: 'whose around solemnly', transportTariff: 26416.79, deliveryScope: 17195.87 };

  let statement;
  let positioning;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/positionings',
      body: { latitude: -6.52, longitude: -86.22 },
    }).then(({ body }) => {
      positioning = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/statements+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/statements').as('postEntityRequest');
    cy.intercept('DELETE', '/api/statements/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/statement-types', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/products', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/positionings', {
      statusCode: 200,
      body: [positioning],
    });

    cy.intercept('GET', '/api/trips', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (statement) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/statements/${statement.id}`,
      }).then(() => {
        statement = undefined;
      });
    }
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

  it('Statements menu should load Statements page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('statement');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Statement').should('exist');
    cy.url().should('match', statementPageUrlPattern);
  });

  describe('Statement page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(statementPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Statement page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/statement/new$'));
        cy.getEntityCreateUpdateHeading('Statement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statementPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/statements',
          body: {
            ...statementSample,
            positioning,
          },
        }).then(({ body }) => {
          statement = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/statements+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/statements?page=0&size=20>; rel="last",<http://localhost/api/statements?page=0&size=20>; rel="first"',
              },
              body: [statement],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(statementPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Statement page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('statement');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statementPageUrlPattern);
      });

      it('edit button click should load edit Statement page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Statement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statementPageUrlPattern);
      });

      it('edit button click should load edit Statement page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Statement');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statementPageUrlPattern);
      });

      it('last delete button click should delete instance of Statement', () => {
        cy.intercept('GET', '/api/statements/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('statement').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statementPageUrlPattern);

        statement = undefined;
      });
    });
  });

  describe('new Statement page', () => {
    beforeEach(() => {
      cy.visit(`${statementPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Statement');
    });

    it('should create an instance of Statement', () => {
      cy.get(`[data-cy="name"]`).type('reclassify boo whenever');
      cy.get(`[data-cy="name"]`).should('have.value', 'reclassify boo whenever');

      cy.get(`[data-cy="transportTariff"]`).type('13157.21');
      cy.get(`[data-cy="transportTariff"]`).should('have.value', '13157.21');

      cy.get(`[data-cy="deliveryScope"]`).type('29906.27');
      cy.get(`[data-cy="deliveryScope"]`).should('have.value', '29906.27');

      cy.get(`[data-cy="positioning"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        statement = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', statementPageUrlPattern);
    });
  });
});
