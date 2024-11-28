import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './statement.reducer';

export const StatementDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const statementEntity = useAppSelector(state => state.statement.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="statementDetailsHeading">
          <Translate contentKey="accountingImportExportProductsApp.statement.detail.title">Statement</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{statementEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="accountingImportExportProductsApp.statement.name">Name</Translate>
            </span>
          </dt>
          <dd>{statementEntity.name}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.statement.statementType">Statement Type</Translate>
          </dt>
          <dd>{statementEntity.statementType ? statementEntity.statementType.name : ''}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.statement.product">Product</Translate>
          </dt>
          <dd>{statementEntity.product ? statementEntity.product.name : ''}</dd>
          <dt>
            <span id="deliveryScope">
              <Translate contentKey="accountingImportExportProductsApp.statement.deliveryScope">Delivery Scope</Translate>
            </span>
          </dt>
          <dd>{statementEntity.deliveryScope}</dd>
          <dt>
            <span id="transportTariff">
              <Translate contentKey="accountingImportExportProductsApp.statement.transportTariff">Transport Tariff</Translate>
            </span>
          </dt>
          <dd>{statementEntity.transportTariff}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.statement.positioning">Positioning</Translate>
          </dt>
          <dd>{statementEntity.positioning ? statementEntity.positioning.latitude + ', ' + statementEntity.positioning.longitude : ''}</dd>
        </dl>
        <Button tag={Link} to="/statement" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/statement/${statementEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StatementDetail;
