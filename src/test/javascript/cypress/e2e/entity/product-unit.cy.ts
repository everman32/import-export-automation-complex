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

describe('ProductUnit e2e test', () => {
  const productUnitPageUrl = '/product-unit';
  const productUnitPageUrlPattern = new RegExp('/product-unit(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const productUnitSample = { name: 'through until failing', description: 'anaesthetise' };

  let productUnit;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/product-units+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-units').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-units/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (productUnit) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-units/${productUnit.id}`,
      }).then(() => {
        productUnit = undefined;
      });
    }
  });

  it('ProductUnits menu should load ProductUnits page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-unit');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductUnit').should('exist');
    cy.url().should('match', productUnitPageUrlPattern);
  });

  describe('ProductUnit page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productUnitPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductUnit page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-unit/new$'));
        cy.getEntityCreateUpdateHeading('ProductUnit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productUnitPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-units',
          body: productUnitSample,
        }).then(({ body }) => {
          productUnit = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-units+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/product-units?page=0&size=20>; rel="last",<http://localhost/api/product-units?page=0&size=20>; rel="first"',
              },
              body: [productUnit],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(productUnitPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ProductUnit page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productUnit');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productUnitPageUrlPattern);
      });

      it('edit button click should load edit ProductUnit page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductUnit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productUnitPageUrlPattern);
      });

      it('edit button click should load edit ProductUnit page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductUnit');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productUnitPageUrlPattern);
      });

      it('last delete button click should delete instance of ProductUnit', () => {
        cy.intercept('GET', '/api/product-units/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('productUnit').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', productUnitPageUrlPattern);

        productUnit = undefined;
      });
    });
  });

  describe('new ProductUnit page', () => {
    beforeEach(() => {
      cy.visit(`${productUnitPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductUnit');
    });

    it('should create an instance of ProductUnit', () => {
      cy.get(`[data-cy="name"]`).type('printer');
      cy.get(`[data-cy="name"]`).should('have.value', 'printer');

      cy.get(`[data-cy="description"]`).type('metabolite');
      cy.get(`[data-cy="description"]`).should('have.value', 'metabolite');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        productUnit = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', productUnitPageUrlPattern);
    });
  });
});
