import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './product-unit.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProductUnitDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const productUnitEntity = useAppSelector(state => state.productUnit.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productUnitDetailsHeading">
          <Translate contentKey="accountingImportExportProductsApp.productUnit.detail.title">ProductUnit</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{productUnitEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="accountingImportExportProductsApp.productUnit.name">Name</Translate>
            </span>
          </dt>
          <dd>{productUnitEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="accountingImportExportProductsApp.productUnit.description">Description</Translate>
            </span>
          </dt>
          <dd>{productUnitEntity.description}</dd>
        </dl>
        <Button tag={Link} to="/product-unit" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/product-unit/${productUnitEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductUnitDetail;
