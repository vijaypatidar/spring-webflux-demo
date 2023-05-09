# Setup Kubernetes for managing deployment of this project

1. Create namespace

```yml
apiVersion: v1
kind: Namespace
metadata:
  name: spring

```

2. Create configMap

```yml
apiVersion: v1
kind: ConfigMap
metadata:
  name: webflux-config
  namespace: spring
data:
  MONGO_URI: mongodb://root:example@127.0.0.1:27017
  MONGO_DB_NAME: digital
```

3. Create deployment

```yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: webflux-deployment
  namespace: spring
  labels:
    app: webflux
spec:
  replicas: 2
  selector:
    matchLabels:
      app: webflux
  template:
    metadata:
      labels:
        app: webflux
        env: dev
    spec:
      containers:
        - name: webflux
          image: vijaypatidar31/webflux:1.1
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: webflux-config
```
