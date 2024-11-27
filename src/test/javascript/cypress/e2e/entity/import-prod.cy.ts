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

describe('ImportProd e2e test', () => {
  const importProdPageUrl = '/import-prod';
  const importProdPageUrlPattern = new RegExp('/import-prod(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const importProdSample = { arrivalDate: '2022-04-22T04:41:42.706Z' };

  let importProd;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/import-prods+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/import-prods').as('postEntityRequest');
    cy.intercept('DELETE', '/api/import-prods/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (importProd) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/import-prods/${importProd.id}`,
      }).then(() => {
        importProd = undefined;
      });
    }
  });

  it('ImportProds menu should load ImportProds page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('import-prod');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ImportProd').should('exist');
    cy.url().should('match', importProdPageUrlPattern);
  });

  describe('ImportProd page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(importProdPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ImportProd page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/import-prod/new$'));
        cy.getEntityCreateUpdateHeading('ImportProd');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', importProdPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/import-prods',
          body: importProdSample,
        }).then(({ body }) => {
          importProd = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/import-prods+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/import-prods?page=0&size=20>; rel="last",<http://localhost/api/import-prods?page=0&size=20>; rel="first"',
              },
              body: [importProd],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(importProdPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ImportProd page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('importProd');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', importProdPageUrlPattern);
      });

      it('edit button click should load edit ImportProd page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ImportProd');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', importProdPageUrlPattern);
      });

      it('edit button click should load edit ImportProd page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ImportProd');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', importProdPageUrlPattern);
      });

      it('last delete button click should delete instance of ImportProd', () => {
        cy.intercept('GET', '/api/import-prods/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('importProd').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', importProdPageUrlPattern);

        importProd = undefined;
      });
    });
  });

  describe('new ImportProd page', () => {
    beforeEach(() => {
      cy.visit(`${importProdPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ImportProd');
    });

    it('should create an instance of ImportProd', () => {
      cy.get(`[data-cy="arrivalDate"]`).type('2022-04-22T22:22');
      cy.get(`[data-cy="arrivalDate"]`).blur();
      cy.get(`[data-cy="arrivalDate"]`).should('have.value', '2022-04-22T22:22');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        importProd = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', importProdPageUrlPattern);
    });
  });
});
