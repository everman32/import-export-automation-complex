import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <>{/* to avoid warnings when empty */}</>
    <MenuItem icon="asterisk" to="/transport">
      <Translate contentKey="global.menu.entities.transport" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/driver">
      <Translate contentKey="global.menu.entities.driver" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/product">
      <Translate contentKey="global.menu.entities.product" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/trip">
      <Translate contentKey="global.menu.entities.trip" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/import-prod">
      <Translate contentKey="global.menu.entities.importProd" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/export-prod">
      <Translate contentKey="global.menu.entities.exportProd" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/grade">
      <Translate contentKey="global.menu.entities.grade" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/statement">
      <Translate contentKey="global.menu.entities.statement" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/statement-type">
      <Translate contentKey="global.menu.entities.statementType" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/positioning">
      <Translate contentKey="global.menu.entities.positioning" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/product-unit">
      <Translate contentKey="global.menu.entities.productUnit" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
