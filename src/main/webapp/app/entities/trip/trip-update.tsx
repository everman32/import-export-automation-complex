import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { IImportProd } from 'app/shared/model/import-prod.model';
import { getEntities as getStatements } from 'app/entities/statement/statement.reducer';
import { getEntities as getImportProds } from 'app/entities/import-prod/import-prod.reducer';
import { IExportProd } from 'app/shared/model/export-prod.model';
import { getEntities as getExportProds } from 'app/entities/export-prod/export-prod.reducer';
import { ITransport } from 'app/shared/model/transport.model';
import { getEntities as getTransports } from 'app/entities/transport/transport.reducer';
import { IDriver } from 'app/shared/model/driver.model';
import { getEntities as getDrivers } from 'app/entities/driver/driver.reducer';
import { IPositioning } from 'app/shared/model/positioning.model';
import { getEntities as getPositionings } from 'app/entities/positioning/positioning.reducer';
import { getEntity, updateEntity, createEntity, reset } from './trip.reducer';
import { ITrip } from 'app/shared/model/trip.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const TripUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const statements = useAppSelector(state => state.statement.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const importProds = useAppSelector(state => state.importProd.entities);
  const exportProds = useAppSelector(state => state.exportProd.entities);
  const transports = useAppSelector(state => state.transport.entities);
  const drivers = useAppSelector(state => state.driver.entities);
  const positionings = useAppSelector(state => state.positioning.entities);
  const tripEntity = useAppSelector(state => state.trip.entity);
  const loading = useAppSelector(state => state.trip.loading);
  const updating = useAppSelector(state => state.trip.updating);
  const updateSuccess = useAppSelector(state => state.trip.updateSuccess);
  const handleClose = () => {
    props.history.push('/trip' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getStatements({}));
    dispatch(getUsers({}));
    dispatch(getImportProds({}));
    dispatch(getExportProds({}));
    dispatch(getTransports({}));
    dispatch(getDrivers({}));
    dispatch(getPositionings({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const statementValidate = Array.isArray(values.statements)
      ? values.statements.toString()
      : values.statements.trim().split(/,\s*/).map(Number);
    const entity = {
      ...tripEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user.toString()),
      transport: transports.find(it => it.id.toString() === values.transport.toString()),
      driver: drivers.find(it => it.id.toString() === values.driver.toString()),
      statements: statements.filter(statement => statementValidate.indexOf(statement.id) !== -1),
      hubPositioning: positionings.find(it => it.id.toString() === values.hubPositioning?.toString()),
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
          ...tripEntity,
          user: tripEntity?.user?.id,
          transport: tripEntity?.transport?.id,
          driver: tripEntity?.driver?.id,
          statements: tripEntity.statements?.map(statement => statement.id),
          hubPositioning: tripEntity?.hubPositioning?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="accountingImportExportProductsApp.trip.home.createOrEditLabel" data-cy="TripCreateUpdateHeading">
            <Translate contentKey="accountingImportExportProductsApp.trip.home.createOrEditLabel">Create or edit a Trip</Translate>
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
                  id="trip-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('accountingImportExportProductsApp.trip.statement')}
                id="trip-statements"
                name="statements"
                data-cy="statements"
                type="text"
              />
              <ValidatedField
                label={translate('accountingImportExportProductsApp.trip.authorizedCapital')}
                id="trip-authorizedCapital"
                name="authorizedCapital"
                data-cy="authorizedCapital"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('accountingImportExportProductsApp.trip.threshold')}
                id="trip-threshold"
                name="threshold"
                data-cy="threshold"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="trip-transport"
                name="transport"
                data-cy="transport"
                label={translate('accountingImportExportProductsApp.trip.transport')}
                type="select"
              >
                <option value="" key="0" />
                {transports
                  ? transports.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.brand}, {otherEntity.model}, {otherEntity.vin}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="trip-driver"
                name="driver"
                data-cy="driver"
                label={translate('accountingImportExportProductsApp.trip.driver')}
                type="select"
              >
                <option value="" key="0" />
                {drivers
                  ? drivers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.firstname}, {otherEntity.patronymic}, {otherEntity.lastname}, {otherEntity.phone}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="trip-user"
                name="user"
                data-cy="user"
                label={translate('accountingImportExportProductsApp.trip.user')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/trip" replace color="info">
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

export default TripUpdate;
