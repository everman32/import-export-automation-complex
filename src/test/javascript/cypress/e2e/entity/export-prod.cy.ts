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

describe('ExportProd e2e test', () => {
  const exportProdPageUrl = '/export-prod';
  const exportProdPageUrlPattern = new RegExp('/export-prod(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const exportProdSample = { departureDate: '2022-04-22T21:11:32.983Z' };

  let exportProd;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/export-prods+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/export-prods').as('postEntityRequest');
    cy.intercept('DELETE', '/api/export-prods/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (exportProd) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/export-prods/${exportProd.id}`,
      }).then(() => {
        exportProd = undefined;
      });
    }
  });

  it('ExportProds menu should load ExportProds page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('export-prod');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ExportProd').should('exist');
    cy.url().should('match', exportProdPageUrlPattern);
  });

  describe('ExportProd page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(exportProdPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ExportProd page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/export-prod/new$'));
        cy.getEntityCreateUpdateHeading('ExportProd');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', exportProdPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/export-prods',
          body: exportProdSample,
        }).then(({ body }) => {
          exportProd = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/export-prods+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/export-prods?page=0&size=20>; rel="last",<http://localhost/api/export-prods?page=0&size=20>; rel="first"',
              },
              body: [exportProd],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(exportProdPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ExportProd page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('exportProd');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', exportProdPageUrlPattern);
      });

      it('edit button click should load edit ExportProd page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ExportProd');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', exportProdPageUrlPattern);
      });

      it('edit button click should load edit ExportProd page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ExportProd');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', exportProdPageUrlPattern);
      });

      it('last delete button click should delete instance of ExportProd', () => {
        cy.intercept('GET', '/api/export-prods/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('exportProd').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', exportProdPageUrlPattern);

        exportProd = undefined;
      });
    });
  });

  describe('new ExportProd page', () => {
    beforeEach(() => {
      cy.visit(`${exportProdPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ExportProd');
    });

    it('should create an instance of ExportProd', () => {
      cy.get(`[data-cy="departureDate"]`).type('2022-04-22T00:16');
      cy.get(`[data-cy="departureDate"]`).blur();
      cy.get(`[data-cy="departureDate"]`).should('have.value', '2022-04-22T00:16');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        exportProd = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', exportProdPageUrlPattern);
    });
  });
});
