# yet another k8s playground [![Build Status](https://travis-ci.org/daggerok/survey.svg?branch=master)](https://travis-ci.org/daggerok/survey)

Status: IN PROGRESS

## TODO

Implement voting system like that:

```
   User   voting-system    votes-service      H2 or PG
    */        +----+         +-------+         +----+
   /|  <----> | UI | <-----> | Store | <-----> | DB |
   / \        +----+         +-------+         +----+
        HTTP         RSocket            R2DBC
```

## k8s k3d k3s

Install k8s cluster with Rancher tools: k3d + k3s if you don't have DOcker for Mac or Windows:

```bash
# start docker...
brew reinstall k3d
k3d create --api-port 6551 --publish 80:80 --workers 2
# ...
export KUBECONFIG="$(k3d get-kubeconfig --name='k3s-default')"
# ...
k3d stop
k3d delete
```

## k8s dashboard

Install and configure Kubernetes Dashboard if you need:

```bash
brew reinstall kubernetes-cli
```

1. deploy k8s dashboard in k8s in Docker for Mac:

```bash
apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v1.10.1/src/deploy/recommended/kubernetes-dashboard.yaml
```

2. copy token:

```bash
kubectl -n kube-system describe secrets kubernetes-dashboard-token-... | grep 'token:' | awk '{print $2}'
```

3. start proxy

```bash
kubectl proxy
```

4. open k8s dashboard and login using token from previous step:

[http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/](http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/)

## start develop

```bash
brew reinstall skaffold
skaffold dev # or skaffold run --tail
# ...
skaffold delete
```

## deploy in public cloud

Before deploy to public cloud, you need build and push docker images...

```bash
./mvnw clean test jib:build
```

## links

* https://github.com/daggerok/spring-5-examples/blob/0a4468ac5c0544b527c00246b0c3702e53651598/reactor-processors/flux-sink/src/main/java/daggerok/FluxSinkApplication.java
* https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html
* https://kubernetes.github.io/ingress-nginx/examples/
* [YouTube: Dead Easy Kubernetes Workflow (Cloud Next '19)](https://www.youtube.com/watch?v=62GLbBDLiPE)
* [Docker k8s: part 1](https://www.docker.com/blog/designing-your-first-app-kubernetes-overview/)
* [Docker k8s: part 2](https://www.docker.com/blog/designing-your-first-application-kubernetes-processes-part2/)
* https://github.com/kubernetes/dashboard/issues/2954#issuecomment-385354244
* https://www.youtube.com/watch?v=rMB1YXU8vAc
* https://rominirani.com/tutorial-getting-started-with-kubernetes-with-docker-on-mac-7f58467203fd
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/maven-plugin/)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/htmlsingle/#configuration-metadata-annotation-processor)
