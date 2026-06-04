# Projet Chat SR03

## Présentation du projet

Ce projet a été réalisé dans le cadre de l’UV SR03.
L’objectif est de développer une application de discussion multi-utilisateurs permettant de gérer des utilisateurs, des salons de discussion, des invitations et des échanges de messages.

Le projet est composé de deux parties :

* un backend développé avec Spring Boot ;
* un frontend développé avec React et Vite.

Dépôts Git :

* Backend : https://github.com/CharleneJiang6/sr03-projet
* Frontend : https://github.com/CharleneJiang6/sr03-frontend

## Membres du groupe

Le projet a été réalisé par :

* Sarra Boubahri
* Charlène Jiang

## Technologies utilisées

### Backend

Le backend repose sur les technologies suivantes :

* Java
* Spring Boot
* Spring MVC
* Spring Data JPA
* Thymeleaf
* WebSocket / STOMP
* SQLite
* Maven

Spring Boot est utilisé pour structurer l’application et faciliter le développement du backend.
Spring MVC permet de gérer les routes web et les contrôleurs.
Spring Data JPA est utilisé pour la couche de persistance et la communication avec la base de données SQLite.
Thymeleaf est utilisé pour l’interface d’administration côté serveur.
WebSocket/STOMP est utilisé pour préparer la communication temps réel entre les utilisateurs.

### Frontend

Le frontend repose sur les technologies suivantes :

* React
* Vite
* JavaScript
* CSS
* npm

React est utilisé pour construire l’interface utilisateur côté client.
Vite permet de lancer rapidement le projet frontend en environnement de développement.

## Architecture du projet

### Backend

Le backend suit une architecture en couches :

```text
Controller → Service → Repository → Database
```

L’organisation principale du backend est la suivante :

```text
src/main/java/fr.utc.sr03
├── controller
│   ├── ChannelApiController
│   ├── InvitationApiController
│   ├── UserApiController
│   └── WebController
├── model
│   ├── Channel
│   ├── Invitation
│   ├── Message
│   ├── Participation
│   ├── User
│   └── UserDTO
├── repository
│   ├── ChannelRepository
│   ├── InvitationRepository
│   ├── MessageRepository
│   ├── ParticipationRepository
│   └── UserRepository
├── services
│   ├── ChannelService
│   ├── InvitationService
│   ├── MessageService
│   ├── ParticipationService
│   ├── PasswordService
│   └── UserService
├── websocket
│   ├── dto
│   ├── MessageSocket
│   ├── WebSocketConfig
│   ├── WebSocketHandler
│   └── WSController
└── Application
```

Les contrôleurs reçoivent les requêtes HTTP ou WebSocket.
Les services contiennent la logique métier.
Les repositories permettent d’interagir avec la base de données.
Les modèles représentent les entités manipulées par l’application.

### Frontend

Le frontend est organisé dans un dépôt séparé :

```text
sr03-frontend
├── public
├── src
│   ├── assets
│   ├── App.css
│   ├── App.jsx
│   ├── ChatPage.jsx
│   ├── index.css
│   └── main.jsx
├── package.json
└── vite.config.js
```

## Fonctionnalités réalisées

### Interface administrateur

La partie administrateur est fonctionnelle.

Elle permet notamment :

* la connexion administrateur ;
* la gestion d’une session administrateur ;
* la création d’utilisateurs ;
* l’affichage de la liste des utilisateurs ;
* la désactivation d’un utilisateur ;
* la réactivation d’un utilisateur ;
* la suppression d’un utilisateur ;
* l’affichage des utilisateurs désactivés ;


L’interface administrateur est réalisée avec Thymeleaf.

### API utilisateurs

L’API utilisateur permet notamment :

* de récupérer la liste des utilisateurs ;
* de rechercher des utilisateurs selon plusieurs critères ;
* de récupérer un utilisateur par identifiant ;
* de récupérer un utilisateur par adresse mail ;
* de créer un utilisateur ;
* de modifier les informations d’un utilisateur ;
* de modifier le mot de passe d’un utilisateur ;
* de supprimer un utilisateur.

Des vérifications ont été ajoutées, notamment sur l’unicité de l’adresse mail et la sécurité du mot de passe.

### API salons

L’API des salons permet notamment :

* de récupérer les salons ;
* de filtrer les salons selon certains critères ;
* de récupérer un salon par identifiant ;
* de créer un salon ;
* de modifier un salon ;
* de supprimer un salon.

### API invitations

L’API des invitations permet notamment :

* de créer une invitation ;
* de récupérer les invitations reçues par un utilisateur ;
* de récupérer les invitations envoyées par un utilisateur ;
* d’accepter une invitation ;
* de refuser une invitation ;
* de supprimer une invitation.

Lorsqu’une invitation est acceptée, l’objectif est de permettre à l’utilisateur invité de rejoindre le salon concerné.

### WebSocket / STOMP

Une première implémentation WebSocket/STOMP a été mise en place afin de gérer la communication temps réel.

La structure WebSocket contient notamment :

* une configuration WebSocket ;
* un contrôleur WebSocket ;
* un modèle de message socket ;
* des DTO liés aux messages.

Cette partie est encore en cours de stabilisation et de test.

## Fonctionnalités en cours ou incomplètes

Certaines parties du projet sont encore en cours de finalisation :

* la partie utilisateur côté frontend n’est pas encore totalement complète ;
* l’interface utilisateur React est encore en cours d’intégration avec le backend ;
* le test complet de WebSocket avec Postman reste à finaliser ;
* certaines fonctionnalités liées aux participations doivent encore être stabilisées ;
* la logique utilisateur finale reste moins complète que la partie administrateur.

## Base de données

Le projet utilise une base de données SQLite.

La base de données sert à stocker les principales entités du projet :

* utilisateurs ;
* salons ;
* invitations ;
* messages ;
* participations.

## Lancement du projet

### Lancer le backend

Dans le dossier du backend :

```bash
mvn compile
```

Puis lancer l’application depuis l’IDE avec le bouton Run sur la classe principale :

```text
Application.java
```

Le backend se lance sur le port configuré dans le projet Spring Boot.

### Lancer le frontend

Dans le dossier du frontend :

```bash
npm install
npm run dev
```

Le frontend est ensuite accessible en local, généralement à l’adresse suivante :

```text
http://localhost:5173
```

## Organisation du travail

Au début du projet, l’organisation du groupe se faisait principalement par WhatsApp.
Chaque membre informait l’autre de l’avancement de ses tâches, des problèmes rencontrés et des éléments ajoutés au projet.

À partir du 15 mai, l’organisation a été davantage structurée grâce à un backlog partagé sous forme de fichier Excel.
Ce backlog permettait de suivre :

* les tâches à réaliser ;
* l’état d’avancement de chaque tâche ;
* la personne responsable ;
* les éléments terminés ;
* les éléments encore à faire.

Les commits Git ont également permis de suivre l’évolution du projet.


## Répartition du travail

### Sarra Boubahri

Sarra a principalement travaillé sur :

* la mise en place de plusieurs entités Java ;
* la création de repositories ;
* l’API des channels ;
* l’API des invitations ;
* les tests des endpoints Channel et Invitation ;
* la mise en place de WebSocket/STOMP ;
* l’amélioration de l’interface administrateur Thymeleaf ;
* la page de confirmation de création utilisateur ;
* la correction de bugs liés à la suppression utilisateur ;
* la création de certains composants React ;
* la définition d’une charte graphique ;
* la préparation du README et du rapport.

### Charlène Jiang

Charlène a principalement travaillé sur :

* la structure initiale du projet ;
* la base de données ;
* certains modèles et contrôleurs ;
* le workflow de connexion ;
* la sécurité de certains endpoints utilisateur ;
* les tests de l’API utilisateur ;
* la mise en place de composants React ;
* la navigation frontend ;
* la planification de discussion ;
* l’intégration progressive du frontend avec le backend.

## Suivi Git

Le projet a été suivi avec Git et GitHub.
Les commits permettent de visualiser l’évolution du projet.

## Limites du projet

Même si plusieurs fonctionnalités backend et administrateur sont fonctionnelles, le projet présente encore certaines limites :

* l’interface utilisateur finale n’est pas totalement complète ;
* certaines interactions côté utilisateur doivent encore être testées ;
* l’intégration frontend/backend reste partielle ;
* la partie WebSocket nécessite encore des tests plus poussés ;
* la gestion complète des participations doit être finalisée.

## Conclusion

Ce projet nous a permis de mettre en pratique plusieurs notions vues en SR03 : Spring Boot, MVC, REST, persistance avec JPA, interface Thymeleaf, API backend, communication avec un frontend React et mise en place d’une communication temps réel avec WebSocket/STOMP.


