import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getStatementTypes } from 'app/entities/statement-type/statement-type.reducer';
import { getEntities as getProducts } from 'app/entities/product/product.reducer';
import { getEntities as getPositionings } from 'app/entities/positioning/positioning.reducer';
import { getEntities as getTrips } from 'app/entities/trip/trip.reducer';
import { createEntity, getEntity, reset, updateEntity } from './statement.reducer';

export const StatementUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const statementTypes = useAppSelector(state => state.statementType.entities);
  const products = useAppSelector(state => state.product.entities);
  const positionings = useAppSelector(state => state.positioning.entities);
  const trips = useAppSelector(state => state.trip.entities);
  const statementEntity = useAppSelector(state => state.statement.entity);
  const loading = useAppSelector(state => state.statement.loading);
  const updating = useAppSelector(state => state.statement.updating);
  const updateSuccess = useAppSelector(state => state.statement.updateSuccess);

  const handleClose = () => {
    navigate(`/statement${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getStatementTypes({}));
    dispatch(getProducts({}));
    dispatch(getPositionings({}));
    dispatch(getTrips({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.transportTariff !== undefined && typeof values.transportTariff !== 'number') {
      values.transportTariff = Number(values.transportTariff);
    }
    if (values.deliveryScope !== undefined && typeof values.deliveryScope !== 'number') {
      values.deliveryScope = Number(values.deliveryScope);
    }

    const entity = {
      ...statementEntity,
      ...values,
      statementType: statementTypes.find(it => it.id.toString() === values.statementType?.toString()),
      product: products.find(it => it.id.toString() === values.product?.toString()),
      positioning: positionings.find(it => it.id.toString() === values.positioning?.toString()),
      trip: trips.find(it => it.id.toString() === values.trip?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...statementEntity,
          statementType: statementEntity?.statementType?.id,
          product: statementEntity?.product?.id,
          positioning: statementEntity?.positioning?.id,
          trip: statementEntity?.trip?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="accountingImportExportProductsApp.statement.home.createOrEditLabel" data-cy="StatementCreateUpdateHeading">
            <Translate contentKey="accountingImportExportProductsApp.statement.home.createOrEditLabel">
              Create or edit a Statement
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="statement-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('accountingImportExportProductsApp.statement.name')}
                id="statement-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 2, message: translate('entity.validation.minlength', { min: 2 }) },
                }}
              />
              <ValidatedField
                id="statement-statementType"
                name="statementType"
                data-cy="statementType"
                label={translate('accountingImportExportProductsApp.statement.statementType')}
                type="select"
              >
                <option value="" key="0" />
                {statementTypes
                  ? statementTypes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="statement-product"
                name="product"
                data-cy="product"
                label={translate('accountingImportExportProductsApp.statement.product')}
                type="select"
              >
                <option value="" key="0" />
                {products
                  ? products.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('accountingImportExportProductsApp.statement.deliveryScope')}
                id="statement-deliveryScope"
                name="deliveryScope"
                data-cy="deliveryScope"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('accountingImportExportProductsApp.statement.transportTariff')}
                id="statement-transportTariff"
                name="transportTariff"
                data-cy="transportTariff"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="statement-positioning"
                name="positioning"
                data-cy="positioning"
                label={translate('accountingImportExportProductsApp.statement.positioning')}
                type="select"
                required
              >
                <option value="" key="0" />
                {positionings
                  ? positionings.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.latitude}, {otherEntity.longitude}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/statement" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default StatementUpdate;
