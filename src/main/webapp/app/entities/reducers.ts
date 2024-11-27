import transport from 'app/entities/transport/transport.reducer';
import driver from 'app/entities/driver/driver.reducer';
import product from 'app/entities/product/product.reducer';
import trip from 'app/entities/trip/trip.reducer';
import importProd from 'app/entities/import-prod/import-prod.reducer';
import exportProd from 'app/entities/export-prod/export-prod.reducer';
import grade from 'app/entities/grade/grade.reducer';
import statement from 'app/entities/statement/statement.reducer';
import statementType from 'app/entities/statement-type/statement-type.reducer';
import positioning from 'app/entities/positioning/positioning.reducer';
import productUnit from 'app/entities/product-unit/product-unit.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  transport,
  driver,
  product,
  trip,
  importProd,
  exportProd,
  grade,
  statement,
  statementType,
  positioning,
  productUnit,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
